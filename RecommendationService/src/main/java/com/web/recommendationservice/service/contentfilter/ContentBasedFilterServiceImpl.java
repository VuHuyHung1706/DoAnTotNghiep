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

    @Override
    public String generateRecommendationReason(MovieResponse movie, List<UserPreference> userPreferences) {
        List<String> reasons = new ArrayList<>();

        // Get top genre preferences
        List<UserPreference> topGenres = userPreferences.stream()
                .filter(pref -> pref.getGenreId() != null)
                .sorted((p1, p2) -> Double.compare(p2.getPreferenceScore(), p1.getPreferenceScore()))
                .limit(3)
                .collect(Collectors.toList());

        // Get top actor preferences
        List<UserPreference> topActors = userPreferences.stream()
                .filter(pref -> pref.getActorId() != null)
                .sorted((p1, p2) -> Double.compare(p2.getPreferenceScore(), p1.getPreferenceScore()))
                .limit(3)
                .collect(Collectors.toList());

        // Check for matching genres
        if (movie.getGenres() != null) {
            Set<Integer> topGenreIds = topGenres.stream()
                    .map(UserPreference::getGenreId)
                    .collect(Collectors.toSet());

            List<String> matchingGenres = movie.getGenres().stream()
                    .filter(genre -> topGenreIds.contains(genre.getId()))
                    .map(GenreResponse::getName)
                    .collect(Collectors.toList());

            if (!matchingGenres.isEmpty()) {
                if (matchingGenres.size() == 1) {
                    reasons.add("Bạn thích thể loại " + matchingGenres.get(0));
                } else {
                    reasons.add("Bạn thích thể loại " + String.join(", ", matchingGenres));
                }
            }
        }

        // Check for matching actors
        if (movie.getActors() != null) {
            Set<Integer> topActorIds = topActors.stream()
                    .map(UserPreference::getActorId)
                    .collect(Collectors.toSet());

            List<String> matchingActors = movie.getActors().stream()
                    .filter(actor -> topActorIds.contains(actor.getId()))
                    .map(actor -> actor.getFirstName() + " " + actor.getLastName())
                    .collect(Collectors.toList());

            if (!matchingActors.isEmpty()) {
                if (matchingActors.size() == 1) {
                    reasons.add("Có diễn viên " + matchingActors.get(0));
                } else {
                    reasons.add("Có các diễn viên " + String.join(", ", matchingActors));
                }
            }
        }

        // Add rating information if high
        if (movie.getAverageRating() != null && movie.getAverageRating() >= 4.0) {
            reasons.add("Đánh giá cao (" + String.format("%.1f", movie.getAverageRating()) + "/5)");
        }

        // Combine reasons
        if (reasons.isEmpty()) {
            return "Phim được đề xuất dựa trên sở thích của bạn";
        }

        return String.join(" • ", reasons);
    }
}
