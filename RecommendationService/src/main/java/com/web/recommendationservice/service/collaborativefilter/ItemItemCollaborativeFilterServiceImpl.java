package com.web.recommendationservice.service.collaborativefilter;

import com.web.recommendationservice.dto.response.MovieResponse;
import com.web.recommendationservice.dto.response.ReviewResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemItemCollaborativeFilterServiceImpl implements ItemItemCollaborativeFilterService {

    @Value("${recommendation.cf.min-common-users:2}")
    private int minCommonUsers;

    @Value("${recommendation.cf.neighbor-size:5}")
    private int neighborSize;

    @Override
    public double calculateMovieSimilarity(Integer movieId1, Integer movieId2,
                                           Map<String, Map<Integer, Integer>> userRatingsMatrix) {
        // Find users who rated both movies
        List<String> commonUsers = new ArrayList<>();
        Map<String, Integer> movie1Ratings = new HashMap<>();
        Map<String, Integer> movie2Ratings = new HashMap<>();

        for (Map.Entry<String, Map<Integer, Integer>> entry : userRatingsMatrix.entrySet()) {
            String username = entry.getKey();
            Map<Integer, Integer> ratings = entry.getValue();

            if (ratings.containsKey(movieId1) && ratings.containsKey(movieId2)) {
                commonUsers.add(username);
                movie1Ratings.put(username, ratings.get(movieId1));
                movie2Ratings.put(username, ratings.get(movieId2));
            }
        }

        // Need at least minCommonUsers to calculate similarity
        if (commonUsers.size() < minCommonUsers) {
            return 0.0;
        }

        // Calculate mean ratings for each movie (from common users only)
        double mean1 = movie1Ratings.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        double mean2 = movie2Ratings.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // Calculate Pearson Correlation Coefficient
        double numerator = 0.0;
        double sumSquares1 = 0.0;
        double sumSquares2 = 0.0;

        for (String user : commonUsers) {
            double diff1 = movie1Ratings.get(user) - mean1;
            double diff2 = movie2Ratings.get(user) - mean2;

            numerator += diff1 * diff2;
            sumSquares1 += diff1 * diff1;
            sumSquares2 += diff2 * diff2;
        }

        // Avoid division by zero
        if (sumSquares1 == 0.0 || sumSquares2 == 0.0) {
            return 0.0;
        }

        double denominator = Math.sqrt(sumSquares1) * Math.sqrt(sumSquares2);
        return numerator / denominator;
    }

    @Override
    public Map<Integer, Double> getRecommendations(String username,
                                                   List<MovieResponse> allMovies,
                                                   List<ReviewResponse> allReviews,
                                                   int topN) {
        // Build user-item ratings matrix
        Map<String, Map<Integer, Integer>> userRatingsMatrix = buildRatingsMatrix(allReviews);

        // Get movies rated by the target user
        Map<Integer, Integer> userRatings = userRatingsMatrix.getOrDefault(username, new HashMap<>());

        if (userRatings.isEmpty()) {
            return new HashMap<>(); // No ratings, cannot recommend
        }

        // Get all movie IDs
        Set<Integer> allMovieIds = allMovies.stream()
                .map(MovieResponse::getId)
                .collect(Collectors.toSet());

        // Find movies not yet rated by user
        Set<Integer> candidateMovies = new HashSet<>(allMovieIds);
        candidateMovies.removeAll(userRatings.keySet());

        // Calculate predicted ratings for candidate movies
        Map<Integer, Double> predictedRatings = new HashMap<>();

        for (Integer candidateMovieId : candidateMovies) {
            double predictedRating = predictRating(candidateMovieId, userRatings,
                    userRatingsMatrix, allMovieIds);

            if (predictedRating > 0) {
                predictedRatings.put(candidateMovieId, predictedRating);
            }
        }

        // Sort by predicted rating and return top N
        return predictedRatings.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Predict rating for a movie using Item-Item CF
     * Formula: r_xi = Σ(s_ij * r_xj) / Σ|s_ij|
     * where s_ij is similarity between items i and j
     * and r_xj is user x's rating for item j
     */
    private double predictRating(Integer targetMovieId,
                                 Map<Integer, Integer> userRatings,
                                 Map<String, Map<Integer, Integer>> userRatingsMatrix,
                                 Set<Integer> allMovieIds) {
        // Find similar movies that user has rated
        List<MovieSimilarity> similarMovies = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : userRatings.entrySet()) {
            Integer ratedMovieId = entry.getKey();
            Integer rating = entry.getValue();

            // Calculate similarity between target movie and rated movie
            double similarity = calculateMovieSimilarity(targetMovieId, ratedMovieId,
                    userRatingsMatrix);

            if (similarity > 0) {
                similarMovies.add(new MovieSimilarity(ratedMovieId, rating, similarity));
            }
        }

        // Need at least one similar movie
        if (similarMovies.isEmpty()) {
            return 0.0;
        }

        // Sort by similarity and take top N neighbors
        similarMovies.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        List<MovieSimilarity> neighbors = similarMovies.stream()
                .limit(neighborSize)
                .collect(Collectors.toList());

        // Calculate weighted average
        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (MovieSimilarity neighbor : neighbors) {
            weightedSum += neighbor.similarity * neighbor.rating;
            similaritySum += Math.abs(neighbor.similarity);
        }

        // Avoid division by zero
        if (similaritySum == 0.0) {
            return 0.0;
        }

        return weightedSum / similaritySum;
    }

    /**
     * Build user-item ratings matrix from reviews
     */
    private Map<String, Map<Integer, Integer>> buildRatingsMatrix(List<ReviewResponse> allReviews) {
        Map<String, Map<Integer, Integer>> matrix = new HashMap<>();

        for (ReviewResponse review : allReviews) {
            String username = review.getUsername();
            Integer movieId = review.getMovieId();
            Integer rating = review.getRating();

            // Skip invalid ratings
            if (rating == null || rating == 0) {
                continue;
            }

            matrix.computeIfAbsent(username, k -> new HashMap<>())
                    .put(movieId, rating);
        }

        return matrix;
    }

    /**
     * Helper class to store movie similarity information
     */
    private static class MovieSimilarity {
        Integer movieId;
        Integer rating;
        double similarity;

        MovieSimilarity(Integer movieId, Integer rating, double similarity) {
            this.movieId = movieId;
            this.rating = rating;
            this.similarity = similarity;
        }
    }
}
