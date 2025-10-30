package com.web.recommendationservice.service.collaborativefilter;

import com.web.recommendationservice.dto.response.MovieResponse;
import com.web.recommendationservice.dto.response.ReviewResponse;

import java.util.List;
import java.util.Map;

public interface ItemItemCollaborativeFilterService {
    /**
     * Calculate similarity between two movies based on user ratings
     * Using Pearson Correlation Coefficient
     */
    double calculateMovieSimilarity(Integer movieId1, Integer movieId2,
                                    Map<String, Map<Integer, Integer>> userRatingsMatrix);

    /**
     * Get movie recommendations for a user using Item-Item Collaborative Filtering
     * @param username The user to recommend movies for
     * @param allMovies All available movies
     * @param allReviews All user reviews
     * @param topN Number of recommendations to return
     * @return List of recommended movie IDs with predicted ratings
     */
    Map<Integer, Double> getRecommendations(String username,
                                            List<MovieResponse> allMovies,
                                            List<ReviewResponse> allReviews,
                                            int topN);
}
