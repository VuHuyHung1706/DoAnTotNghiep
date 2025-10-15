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
import com.web.recommendationservice.service.contentfilter.ContentBasedFilterService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private MovieServiceClient movieServiceClient;

    @Autowired
    private BookingServiceClient bookingServiceClient;

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
    public List<UserRecommendationResponse> getUsersForMovie(Integer movieId) {
        // Get the movie details
        ApiResponse<MovieResponse> movieResponse = movieServiceClient.getMovieById(movieId);
        MovieResponse movie = movieResponse.getResult();

        if (movie == null) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        // Get all users who have preferences
        List<String> allUsernames = userPreferenceRepository.findAllDistinctUsernames();

        if (allUsernames.isEmpty()) {
            throw new AppException(ErrorCode.INSUFFICIENT_DATA);
        }

        // Calculate similarity score for each user
        List<UserRecommendationResponse> userRecommendations = new ArrayList<>();

        for (String username : allUsernames) {
            // Get user preferences
            List<UserPreference> userPreferences = userPreferenceRepository.findByUsername(username);

            if (userPreferences.isEmpty()) {
                continue;
            }

            // Check if user has already watched this movie
            Set<Integer> watchedMovieIds = getWatchedMovieIds(username);
            if (watchedMovieIds.contains(movieId)) {
                continue; // Skip users who already watched this movie
            }

            // Calculate similarity score
            double similarityScore = contentBasedFilterService.calculateSimilarityScore(
                    movie, userPreferences);

            if (similarityScore >= minSimilarityScore) {
                String reason = contentBasedFilterService.generateRecommendationReason(
                        movie, userPreferences);

                userRecommendations.add(UserRecommendationResponse.builder()
                        .username(username)
                        .similarityScore(similarityScore)
                        .reason(reason)
                        .build());
            }
        }

        // Sort by similarity score (descending) and limit results
        userRecommendations.sort((u1, u2) -> Double.compare(u2.getSimilarityScore(), u1.getSimilarityScore()));

        return userRecommendations.stream()
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponse> getRecommendationsForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Update user preferences based on latest interactions
        updateUserPreferencesInternal(username);

        // Get user preferences
        List<UserPreference> userPreferences = userPreferenceRepository.findByUsername(username);

        if (userPreferences.isEmpty()) {
            throw new AppException(ErrorCode.NO_USER_HISTORY);
        }

        // Get all available movies
        ApiResponse<List<MovieResponse>> moviesResponse = movieServiceClient.getAllMovies();
        List<MovieResponse> allMovies = moviesResponse.getResult();

        if (allMovies == null || allMovies.isEmpty()) {
            throw new AppException(ErrorCode.INSUFFICIENT_DATA);
        }

        // Get movies user has already watched (from bookings)
        Set<Integer> watchedMovieIds = getWatchedMovieIds(username);

        // Filter out already watched movies
        List<MovieResponse> candidateMovies = allMovies.stream()
                .filter(movie -> !watchedMovieIds.contains(movie.getId()))
                .collect(Collectors.toList());

        // Calculate similarity scores for each candidate movie
        List<RecommendationResponse> recommendations = new ArrayList<>();

        for (MovieResponse movie : candidateMovies) {
            double similarityScore = contentBasedFilterService.calculateSimilarityScore(
                    movie, userPreferences);

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

    private void updateUserPreferencesInternal(String username) {
        // Get user's booking history
        ApiResponse<List<BookingResponse>> bookingsResponse = bookingServiceClient.getBookingByUsername(username);
        List<BookingResponse> bookings = bookingsResponse.getResult();

        // Get user's reviews
        ApiResponse<List<ReviewResponse>> reviewsResponse = movieServiceClient.getReviewsByUsername(username);
        List<ReviewResponse> reviews = reviewsResponse.getResult();

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
            ApiResponse<List<BookingResponse>> bookingsResponse = bookingServiceClient.getBookingByUsername(username);
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
