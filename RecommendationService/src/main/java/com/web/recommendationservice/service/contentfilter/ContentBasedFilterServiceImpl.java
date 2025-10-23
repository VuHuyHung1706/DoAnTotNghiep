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

    @Value("${recommendation.rating-weight}")
    private double ratingWeight;

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

        // Calculate genre similarity
        double genreSimilarity = 0.0;
        if (movie.getGenres() != null && !movie.getGenres().isEmpty() && !genreScores.isEmpty()) {
            double maxGenreScore = Collections.max(genreScores.values());
            
            for (GenreResponse genre : movie.getGenres()) {
                if (genreScores.containsKey(genre.getId())) {
                    // Normalize score to 0-1 range
                    genreSimilarity += genreScores.get(genre.getId()) / maxGenreScore;
                }
            }
            // Average across all genres in the movie
            genreSimilarity = genreSimilarity / movie.getGenres().size();
        }

        // Calculate actor similarity
        double actorSimilarity = 0.0;
        if (movie.getActors() != null && !movie.getActors().isEmpty() && !actorScores.isEmpty()) {
            double maxActorScore = Collections.max(actorScores.values());
            
            for (ActorResponse actor : movie.getActors()) {
                if (actorScores.containsKey(actor.getId())) {
                    // Normalize score to 0-1 range
                    actorSimilarity += actorScores.get(actor.getId()) / maxActorScore;
                }
            }
            // Average across all actors in the movie
            actorSimilarity = actorSimilarity / movie.getActors().size();
        }

        // Calculate rating boost (movies with higher ratings get a boost)
        double ratingBoost = 0.0;
        if (movie.getAverageRating() != null && movie.getAverageRating() > 0) {
            // Normalize rating to 0-1 range (assuming 5-star scale)
            ratingBoost = movie.getAverageRating() / 5.0;
        }

        // Weighted combination of all factors
        double totalScore = (genreSimilarity * genreWeight) + 
                           (actorSimilarity * actorWeight) + 
                           (ratingBoost * ratingWeight);

        // Normalize to 0-1 range
        double totalWeight = genreWeight + actorWeight + ratingWeight;
        return totalScore / totalWeight;
    }
}
