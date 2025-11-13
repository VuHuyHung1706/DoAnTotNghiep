package com.web.recommendationservice.service.recommendation;

import com.web.recommendationservice.dto.response.*;
import com.web.recommendationservice.entity.UserPreference;
import com.web.recommendationservice.exception.AppException;
import com.web.recommendationservice.exception.ErrorCode;
import com.web.recommendationservice.repository.UserPreferenceRepository;
import com.web.recommendationservice.repository.client.BookingServiceClient;
import com.web.recommendationservice.repository.client.MovieServiceClient;
import com.web.recommendationservice.service.collaborativefilter.ItemItemCollaborativeFilterService;
import com.web.recommendationservice.service.contentfilter.ContentBasedFilterService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ContentBasedFilterService contentBasedFilterService;

    @Autowired
    private ItemItemCollaborativeFilterService itemItemCFService;

    @Value("${recommendation.default-rating}")
    private double defaultRating;


    @Value("${recommendation.max-results}")
    private int maxResults;


    @Value("${recommendation.min-similarity-score}")
    private double minSimilarityScore;

    @Value("${recommendation.cf.enabled:true}")
    private boolean cfEnabled;

    @Override
    public List<MovieRecommendationResponse> getMovieRecommendationsForUser(String username) {
        // Get all movies
        ApiResponse<List<MovieResponse>> moviesResponse = movieServiceClient.getAllMovies();
        List<MovieResponse> allMovies = moviesResponse.getResult();

        if (allMovies == null || allMovies.isEmpty()) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        // Get all reviews for collaborative filtering
        ApiResponse<List<ReviewResponse>> reviewsResponse = movieServiceClient.getAllReviews();

        List<ReviewResponse> allReviews = reviewsResponse.getResult();

        // Get movies already watched by user
        Set<Integer> watchedMovieIds = getWatchedMovieIds(username);

        List<MovieRecommendationResponse> recommendations = new ArrayList<>();

        Map<Integer, Double> cfRecommendations = itemItemCFService.getRecommendations(
                username, allMovies, allReviews);

        for (Map.Entry<Integer, Double> entry : cfRecommendations.entrySet()) {
            Integer movieId = entry.getKey();
            Double cfScore = entry.getValue();

            // Skip already watched movies
            if (watchedMovieIds.contains(movieId)) {
                continue;
            }

            // Get movie details
            MovieResponse movie = allMovies.stream()
                    .filter(m -> m.getId().equals(movieId))
                    .findFirst()
                    .orElse(null);

            if (movie == null) {
                continue;
            }

            recommendations.add(MovieRecommendationResponse.builder()
                    .movieId(movieId)
                    .movieTitle(movie.getTitle())
                    .predictedRating(cfScore)
                    .build());
        }
        // Sort by score and limit results
        return recommendations.stream()
                .sorted((r1, r2) -> Double.compare(r2.getPredictedRating(), r1.getPredictedRating()))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

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
                userRecommendations.add(UserRecommendationResponse.builder()
                        .username(username)
                        .similarityScore(similarityScore)
                        .build());
            }
        }

        // Sort by similarity score (descending) and limit results
        userRecommendations.sort((u1, u2) -> Double.compare(u2.getSimilarityScore(), u1.getSimilarityScore()));

        return new ArrayList<>(userRecommendations);
    }

    @Override
    public void updateUserPreferencesInternal(String username) {
        // Get user's booking history
        ApiResponse<List<BookingResponse>> bookingsResponse = bookingServiceClient.getPaidBookingByUsername(username);
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
                    ReviewResponse review = reviews.stream()
                            .filter(r -> r.getMovieId().equals(movie.getId()))
                            .findFirst()
                            .orElse(null);

                    double rating = (review != null) ? Double.valueOf(review.getRating()) : defaultRating;

                    // Process genres
                    if (movie.getGenres() != null) {
                        for (GenreResponse genre : movie.getGenres()) {
                            updateGenrePreference(genrePreferences, genre, rating);
                        }
                    }

                    // Process actors
                    if (movie.getActors() != null) {
                        for (ActorResponse actor : movie.getActors()) {
                            updateActorPreference(actorPreferences, actor, rating);
                        }
                    }
                }
            }
        }

        // Sum the user preference score

        double sumPerferenceScores = 0;
        for (UserPreference userPreference : genrePreferences.values()) {
            sumPerferenceScores = sumPerferenceScores + userPreference.getPreferenceScore();
        }

        for (UserPreference userPreference : actorPreferences.values()) {
            sumPerferenceScores = sumPerferenceScores + userPreference.getPreferenceScore();
        }

        // Save or update preferences in database
        for (UserPreference genrePref : genrePreferences.values()) {
            Optional<UserPreference> existing = userPreferenceRepository
                    .findByUsernameAndGenreId(username, genrePref.getGenreId());

            if (existing.isPresent()) {
                UserPreference pref = existing.get();
                pref.setPreferenceScore(genrePref.getPreferenceScore() / sumPerferenceScores);
                pref.setLastUpdated(LocalDateTime.now());
                userPreferenceRepository.save(pref);
            } else {
                genrePref.setUsername(username);
                genrePref.setPreferenceScore(genrePref.getPreferenceScore() / sumPerferenceScores);
                userPreferenceRepository.save(genrePref);
            }
        }

        for (UserPreference actorPref : actorPreferences.values()) {
            Optional<UserPreference> existing = userPreferenceRepository
                    .findByUsernameAndActorId(username, actorPref.getActorId());

            if (existing.isPresent()) {
                UserPreference pref = existing.get();
                pref.setPreferenceScore(actorPref.getPreferenceScore() / sumPerferenceScores) ;
                pref.setLastUpdated(LocalDateTime.now());
                userPreferenceRepository.save(pref);
            } else {
                actorPref.setUsername(username);
                actorPref.setPreferenceScore(actorPref.getPreferenceScore() / sumPerferenceScores);
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
                    .build();
            preferences.put(genre.getId(), pref);
        } else {
            pref.setPreferenceScore(pref.getPreferenceScore() + weight);
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
                    .build();
            preferences.put(actor.getId(), pref);
        } else {
            pref.setPreferenceScore(pref.getPreferenceScore() + weight);
        }
    }

    private Set<Integer> getWatchedMovieIds(String username) {
        Set<Integer> watchedIds = new HashSet<>();

        try {
            ApiResponse<List<BookingResponse>> bookingsResponse = bookingServiceClient.getPaidBookingByUsername(username);
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
