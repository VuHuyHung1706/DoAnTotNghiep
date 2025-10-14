package com.web.recommendationservice.service.recommendation;

import com.web.recommendationservice.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    List<RecommendationResponse> getRecommendationsForUser();
    void updateUserPreferences();
    void trackRecommendationClick(Integer movieId);
}
