package com.web.recommendationservice.service.recommendation;

import com.web.recommendationservice.dto.response.RecommendationResponse;
import com.web.recommendationservice.dto.response.UserRecommendationResponse;

import java.util.List;

public interface RecommendationService {
    void updateUserPreferencesInternal(String username);
    List<UserRecommendationResponse> getUsersForMovie(Integer movieId);
    List<RecommendationResponse> getMovieRecommendationsForUser(String username);
}
