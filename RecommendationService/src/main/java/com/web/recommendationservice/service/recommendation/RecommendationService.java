package com.web.recommendationservice.service.recommendation;

import com.web.recommendationservice.dto.response.MovieRecommendationResponse;
import com.web.recommendationservice.dto.response.UserRecommendationResponse;

import java.util.List;

public interface RecommendationService {
    void updateUserPreferencesInternal(String username);
    List<UserRecommendationResponse> getUsersForMovie(Integer movieId);
    List<MovieRecommendationResponse> getMovieRecommendations(String username);
    List<MovieRecommendationResponse> getMovieRecommendationsUsingItemItem(String username);
    List<MovieRecommendationResponse> getMovieRecommendationsUsingCB(String username);
}
