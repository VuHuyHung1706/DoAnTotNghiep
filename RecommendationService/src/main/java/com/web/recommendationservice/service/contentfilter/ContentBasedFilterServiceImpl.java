package com.web.recommendationservice.service.contentfilter;

import com.web.recommendationservice.dto.response.ActorResponse;
import com.web.recommendationservice.dto.response.GenreResponse;
import com.web.recommendationservice.dto.response.MovieResponse;
import com.web.recommendationservice.entity.UserPreference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentBasedFilterServiceImpl implements ContentBasedFilterService {

    @Value("${recommendation.genre-weight}")
    private double genreWeight;

    @Value("${recommendation.actor-weight}")
    private double actorWeight;

//    @Value("${recommendation.rating-weight}")
//    private double ratingWeight;

    @Override
    public double calculateSimilarityScore(MovieResponse movie, List<UserPreference> userPreferences) {
        // Separate genre and actor preferences
        Map<Integer, Double> genreScores = userPreferences.stream()
                .filter(pref -> pref.getGenreId() != null)
                .collect(Collectors.toMap(
                        UserPreference::getGenreId,
                        UserPreference::getPreferenceScore,
                        Double::sum
                ));

        Map<Integer, Double> actorScores = userPreferences.stream()
                .filter(pref -> pref.getActorId() != null)
                .collect(Collectors.toMap(
                        UserPreference::getActorId,
                        UserPreference::getPreferenceScore,
                        Double::sum
                ));

        // Build user preference vector and movie feature vector
        Map<String, Double> userVector = new HashMap<>();
        Map<String, Double> movieVector = new HashMap<>();

        // Add genre features to vectors
        if (movie.getGenres() != null) {
            for (GenreResponse genre : movie.getGenres()) {
                String key = "genre_" + genre.getId();
                movieVector.put(key, genreWeight);
                userVector.put(key, genreScores.getOrDefault(genre.getId(), 0.0) * genreWeight);
            }
        }

        // Add all user genre preferences to user vector
        for (Map.Entry<Integer, Double> entry : genreScores.entrySet()) {
            String key = "genre_" + entry.getKey();
            if (!userVector.containsKey(key)) {
                userVector.put(key, entry.getValue() * genreWeight);
                movieVector.put(key, 0.0);
            }
        }

        // Add actor features to vectors
        if (movie.getActors() != null) {
            for (ActorResponse actor : movie.getActors()) {
                String key = "actor_" + actor.getId();
                movieVector.put(key, actorWeight);
                userVector.put(key, actorScores.getOrDefault(actor.getId(), 0.0) * actorWeight);
            }
        }
        // Add all user actor preferences to user vector
        for (Map.Entry<Integer, Double> entry : actorScores.entrySet()) {
            String key = "actor_" + entry.getKey();
            if (!userVector.containsKey(key)) {
                userVector.put(key, entry.getValue() * actorWeight);
                movieVector.put(key, 0.0);
            }
        }


        // Calculate cosine similarity: (x·i) / (||x|| · ||i||)
        double dotProduct = 0.0;
        double userMagnitude = 0.0;
        double movieMagnitude = 0.0;

        // Get all unique keys from both vectors
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(userVector.keySet());
        allKeys.addAll(movieVector.keySet());

        // Calculate dot product and magnitudes
        for (String key : allKeys) {
            double userValue = userVector.getOrDefault(key, 0.0);
            double movieValue = movieVector.getOrDefault(key, 0.0);

            dotProduct += userValue * movieValue;
            userMagnitude += userValue * userValue;
            movieMagnitude += movieValue * movieValue;
        }

        // Calculate final cosine similarity
        userMagnitude = Math.sqrt(userMagnitude);
        movieMagnitude = Math.sqrt(movieMagnitude);

        // Avoid division by zero
        if (userMagnitude == 0.0 || movieMagnitude == 0.0) {
            return 0.0;
        }

        return dotProduct / (userMagnitude * movieMagnitude);
    }
}