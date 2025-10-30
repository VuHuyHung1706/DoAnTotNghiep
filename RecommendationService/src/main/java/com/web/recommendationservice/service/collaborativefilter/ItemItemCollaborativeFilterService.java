package com.web.recommendationservice.service.collaborativefilter;

import com.web.recommendationservice.dto.response.MovieResponse;
import com.web.recommendationservice.dto.response.ReviewResponse;

import java.util.List;
import java.util.Map;

public interface ItemItemCollaborativeFilterService {
    double calculateMovieSimilarity(Integer movieId1, Integer movieId2,
                                    Map<String, Map<Integer, Integer>> userRatingsMatrix);

    Map<Integer, Double> getRecommendations(String username,
                                            List<MovieResponse> allMovies,
                                            List<ReviewResponse> allReviews);
}
