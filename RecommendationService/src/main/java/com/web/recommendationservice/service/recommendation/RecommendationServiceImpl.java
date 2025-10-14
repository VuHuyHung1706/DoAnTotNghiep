package com.web.recommendationservice.service.recommendation;

import com.web.recommendationservice.dto.response.*;
import com.web.recommendationservice.entity.RecommendationHistory;
import com.web.recommendationservice.entity.UserPreference;
import com.web.recommendationservice.exception.AppException;
import com.web.recommendationservice.exception.ErrorCode;
import com.web.recommendationservice.repository.RecommendationHistoryRepository;
import com.web.recommendationservice.repository.UserPreferenceRepository;
import com.web.recommendationservice.repository.client.BookingServiceClient;
import com.web.recommendationservice.repository.client.MovieServiceClient;
import com.web.recommendationservice.repository.client.UserServiceClient;
import com.web.recommendationservice.service.contentfilter.ContentBasedFilterService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private MovieServiceClient movieServiceClient;

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private RecommendationHistoryRepository recommendationHistoryRepository;

    @Autowired
    private ContentBasedFilterService contentBasedFilterService;

    @Value("${recommendation.max-results}")
    private int maxResults;

    @Value("${recommendation.min-similarity-score}")
    private double minSimilarityScore;

    @Override
    public List<RecommendationResponse> getRecommendationsForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[v0] Getting recommendations for user: {}", username);

        // Update user preferences based on latest interactions
        updateUserPreferencesInternal(username);

        // Get user preferences
        List<UserPreference> userPreferences = userPreferenceRepository.findByUsername(username);
        log.info("[v0] Found {} user preferences", userPreferences.size());

        if (userPreferences.isEmpty()) {
            log.info("[v0] No user history found, returning popular movies");
            return getPopularMovies();
        }

        // Get all available movies
        ApiResponse<List<MovieResponse>> moviesResponse = movieServiceClient.getAllMovies();
        List<MovieResponse> allMovies = moviesResponse.getResult();
        log.info("[v0] Retrieved {} movies from MovieService", allMovies != null ? allMovies.size() : 0);

        if (allMovies == null || allMovies.isEmpty()) {
            throw new AppException(ErrorCode.INSUFFICIENT_DATA);
        }

        // Get movies user has already watched (from bookings)
        Set<Integer> watchedMovieIds = getWatchedMovieIds(username);
        log.info("[v0] User has watched {} movies", watchedMovieIds.size());

        // Filter out already watched movies
        List<MovieResponse> candidateMovies = allMovies.stream()
                .filter(movie -> !watchedMovieIds.contains(movie.getId()))
                .collect(Collectors.toList());
        log.info("[v0] {} candidate movies after filtering watched", candidateMovies.size());

        // Calculate similarity scores for each candidate movie
        List<RecommendationResponse> recommendations = new ArrayList<>();

        for (MovieResponse movie : candidateMovies) {
            double similarityScore = contentBasedFilterService.calculateSimilarityScore(
                    movie, userPreferences);

            log.debug("[v0] Movie '{}' similarity score: {}", movie.getTitle(), similarityScore);

            if (similarityScore >= minSimilarityScore) {
                String reason = contentBasedFilterService.generateRecommendationReason(
                        movie, userPreferences);

                recommendations.add(RecommendationResponse.builder()
                        .movie(movie)
                        .similarityScore(similarityScore)
                        .reason(reason)
                        .build());
            }
        }

        log.info("[v0] Generated {} recommendations above threshold", recommendations.size());

        if (recommendations.isEmpty()) {
            log.info("[v0] No recommendations above threshold, returning popular movies");
            return getPopularMovies();
        }

        // Sort by similarity score (descending) and limit results
        recommendations.sort((r1, r2) -> Double.compare(r2.getSimilarityScore(), r1.getSimilarityScore()));

        List<RecommendationResponse> topRecommendations = recommendations.stream()
                .limit(maxResults)
                .collect(Collectors.toList());

        // Save recommendation history
        for (RecommendationResponse recommendation : topRecommendations) {
            if (!recommendationHistoryRepository.existsByUsernameAndMovieId(
                    username, recommendation.getMovie().getId())) {

                RecommendationHistory history = RecommendationHistory.builder()
                        .username(username)
                        .movieId(recommendation.getMovie().getId())
                        .similarityScore(recommendation.getSimilarityScore())
                        .recommendedAt(LocalDateTime.now())
                        .build();

                recommendationHistoryRepository.save(history);
            }
        }

        log.info("[v0] Returning {} recommendations", topRecommendations.size());
        return topRecommendations;
    }

    @Override
    public void updateUserPreferences() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        updateUserPreferencesInternal(username);
    }

    @Override
    public void trackRecommendationClick(Integer movieId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<RecommendationHistory> histories = recommendationHistoryRepository
                .findByUsername(username);

        for (RecommendationHistory history : histories) {
            if (history.getMovieId().equals(movieId) && !history.getWasClicked()) {
                history.setWasClicked(true);
                recommendationHistoryRepository.save(history);
                break;
            }
        }
    }

    @Override
    public List<UserRecommendationResponse> getUsersForMovie(Integer movieId) {
        log.info("[v0] Finding suitable users for movie ID: {}", movieId);

        // Get movie details
        ApiResponse<MovieResponse> movieResponse = movieServiceClient.getMovieById(movieId);
        MovieResponse movie = movieResponse.getResult();

        if (movie == null) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }

        log.info("[v0] Movie found: {}", movie.getTitle());

        // Get all users
        ApiResponse<List<CustomerResponse>> usersResponse = userServiceClient.getAllCustomers();
        List<CustomerResponse> allUsers = usersResponse.getResult();

        if (allUsers == null || allUsers.isEmpty()) {
            log.warn("[v0] No users found in system");
            return new ArrayList<>();
        }

        log.info("[v0] Found {} users to analyze", allUsers.size());

        List<UserRecommendationResponse> userRecommendations = new ArrayList<>();

        // For each user, calculate how well the movie matches their preferences
        for (CustomerResponse user : allUsers) {
            try {
                // Get user preferences
                List<UserPreference> userPreferences = userPreferenceRepository
                        .findByUsername(user.getUsername());

                if (userPreferences.isEmpty()) {
                    log.debug("[v0] User {} has no preferences, skipping", user.getUsername());
                    continue;
                }

                // Calculate match score
                double matchScore = contentBasedFilterService.calculateSimilarityScore(
                        movie, userPreferences);

                log.debug("[v0] User {} match score: {}", user.getUsername(), matchScore);

                if (matchScore >= minSimilarityScore) {
                    String reason = contentBasedFilterService.generateRecommendationReason(
                            movie, userPreferences);

                    userRecommendations.add(UserRecommendationResponse.builder()
                            .user(user)
                            .matchScore(matchScore)
                            .reason(reason)
                            .build());
                }
            } catch (Exception e) {
                log.error("[v0] Error processing user {}: {}", user.getUsername(), e.getMessage());
            }
        }

        log.info("[v0] Found {} matching users", userRecommendations.size());

        // Sort by match score (descending)
        userRecommendations.sort((u1, u2) ->
                Double.compare(u2.getMatchScore(), u1.getMatchScore()));

        // Return top matches
        return userRecommendations.stream()
                .limit(maxResults * 2) // Return more users for marketing purposes
                .collect(Collectors.toList());
    }

    private List<RecommendationResponse> getPopularMovies() {
        try {
            ApiResponse<List<MovieResponse>> moviesResponse = movieServiceClient.getAllMovies();
            List<MovieResponse> allMovies = moviesResponse.getResult();

            if (allMovies == null || allMovies.isEmpty()) {
                return new ArrayList<>();
            }

            // Sort by rating and review count
            List<MovieResponse> popularMovies = allMovies.stream()
                    .filter(movie -> movie.getAverageRating() != null && movie.getAverageRating() > 0)
                    .sorted((m1, m2) -> {
                        // First compare by rating
                        int ratingCompare = Double.compare(
                                m2.getAverageRating() != null ? m2.getAverageRating() : 0,
                                m1.getAverageRating() != null ? m1.getAverageRating() : 0
                        );
                        if (ratingCompare != 0) return ratingCompare;

                        // Then by review count
                        return Integer.compare(
                                m2.getReviewCount() != null ? m2.getReviewCount() : 0,
                                m1.getReviewCount() != null ? m1.getReviewCount() : 0
                        );
                    })
                    .limit(maxResults)
                    .collect(Collectors.toList());

            // Convert to RecommendationResponse
            return popularMovies.stream()
                    .map(movie -> RecommendationResponse.builder()
                            .movie(movie)
                            .similarityScore(0.0)
                            .reason("Phim phổ biến được đánh giá cao")
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[v0] Error getting popular movies", e);
            return new ArrayList<>();
        }
    }

    private void updateUserPreferencesInternal(String username) {
        log.info("[v0] Updating preferences for user: {}", username);

        // Get user's booking history
        ApiResponse<List<BookingResponse>> bookingsResponse = bookingServiceClient.getMyBookings();
        List<BookingResponse> bookings = bookingsResponse.getResult();
        log.info("[v0] Found {} bookings", bookings != null ? bookings.size() : 0);

        // Get user's reviews
        ApiResponse<List<ReviewResponse>> reviewsResponse = movieServiceClient.getReviewsByUsername(username);
        List<ReviewResponse> reviews = reviewsResponse.getResult();
        log.info("[v0] Found {} reviews", reviews != null ? reviews.size() : 0);

        // Map to store aggregated preferences
        Map<Integer, UserPreference> genrePreferences = new HashMap<>();
        Map<Integer, UserPreference> actorPreferences = new HashMap<>();

        // Process bookings
        if (bookings != null) {
            for (BookingResponse booking : bookings) {
                if (booking.getShowtime() != null && booking.getShowtime().getMovie() != null) {
                    MovieResponse movie = booking.getShowtime().getMovie();

                    // Process genres
                    if (movie.getGenres() != null) {
                        for (GenreResponse genre : movie.getGenres()) {
                            updateGenrePreference(genrePreferences, genre, 1.0);
                        }
                    }

                    // Process actors
                    if (movie.getActors() != null) {
                        for (ActorResponse actor : movie.getActors()) {
                            updateActorPreference(actorPreferences, actor, 1.0);
                        }
                    }
                }
            }
        }

        // Process reviews (higher weight for reviewed movies)
        if (reviews != null) {
            for (ReviewResponse review : reviews) {
                ApiResponse<MovieResponse> movieResponse = movieServiceClient.getMovieById(review.getMovieId());
                MovieResponse movie = movieResponse.getResult();

                if (movie != null) {
                    // Weight based on rating (1-5 scale)
                    double weight = review.getRating() / 5.0 * 2.0; // Scale to 0.4-2.0

                    // Process genres with rating weight
                    if (movie.getGenres() != null) {
                        for (GenreResponse genre : movie.getGenres()) {
                            updateGenrePreference(genrePreferences, genre, weight);
                        }
                    }

                    // Process actors with rating weight
                    if (movie.getActors() != null) {
                        for (ActorResponse actor : movie.getActors()) {
                            updateActorPreference(actorPreferences, actor, weight);
                        }
                    }
                }
            }
        }

        // Save or update preferences in database
        for (UserPreference genrePref : genrePreferences.values()) {
            Optional<UserPreference> existing = userPreferenceRepository
                    .findByUsernameAndGenreId(username, genrePref.getGenreId());

            if (existing.isPresent()) {
                UserPreference pref = existing.get();
                pref.setPreferenceScore(genrePref.getPreferenceScore());
                pref.setInteractionCount(genrePref.getInteractionCount());
                pref.setLastUpdated(LocalDateTime.now());
                userPreferenceRepository.save(pref);
            } else {
                genrePref.setUsername(username);
                userPreferenceRepository.save(genrePref);
            }
        }

        for (UserPreference actorPref : actorPreferences.values()) {
            Optional<UserPreference> existing = userPreferenceRepository
                    .findByUsernameAndActorId(username, actorPref.getActorId());

            if (existing.isPresent()) {
                UserPreference pref = existing.get();
                pref.setPreferenceScore(actorPref.getPreferenceScore());
                pref.setInteractionCount(actorPref.getInteractionCount());
                pref.setLastUpdated(LocalDateTime.now());
                userPreferenceRepository.save(pref);
            } else {
                actorPref.setUsername(username);
                userPreferenceRepository.save(actorPref);
            }
        }
    }

    private void updateGenrePreference(Map<Integer, UserPreference> preferences,
                                       GenreResponse genre, double weight) {
        UserPreference pref = preferences.get(genre.getId());
        if (pref == null) {
            pref = UserPreference.builder()
                    .genreId(genre.getId())
                    .genreName(genre.getName())
                    .preferenceScore(weight)
                    .interactionCount(1)
                    .build();
            preferences.put(genre.getId(), pref);
        } else {
            pref.setPreferenceScore(pref.getPreferenceScore() + weight);
            pref.setInteractionCount(pref.getInteractionCount() + 1);
        }
    }

    private void updateActorPreference(Map<Integer, UserPreference> preferences,
                                       ActorResponse actor, double weight) {
        UserPreference pref = preferences.get(actor.getId());
        if (pref == null) {
            String actorName = actor.getFirstName() + " " + actor.getLastName();
            pref = UserPreference.builder()
                    .actorId(actor.getId())
                    .actorName(actorName)
                    .preferenceScore(weight)
                    .interactionCount(1)
                    .build();
            preferences.put(actor.getId(), pref);
        } else {
            pref.setPreferenceScore(pref.getPreferenceScore() + weight);
            pref.setInteractionCount(pref.getInteractionCount() + 1);
        }
    }

    private Set<Integer> getWatchedMovieIds(String username) {
        Set<Integer> watchedIds = new HashSet<>();

        try {
            ApiResponse<List<BookingResponse>> bookingsResponse = bookingServiceClient.getMyBookings();
            List<BookingResponse> bookings = bookingsResponse.getResult();

            if (bookings != null) {
                for (BookingResponse booking : bookings) {
                    if (booking.getShowtime() != null && booking.getShowtime().getMovie() != null) {
                        watchedIds.add(booking.getShowtime().getMovie().getId());
                    }
                }
            }
        } catch (Exception e) {
            // If booking service fails, continue with empty set
        }

        return watchedIds;
    }
}
