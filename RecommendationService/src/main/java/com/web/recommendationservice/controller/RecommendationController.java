package com.web.recommendationservice.controller;

import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.RecommendationResponse;
import com.web.recommendationservice.dto.response.UserRecommendationResponse;
import com.web.recommendationservice.service.recommendation.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ApiResponse<List<RecommendationResponse>> getRecommendations() {
        return ApiResponse.<List<RecommendationResponse>>builder()
                .result(recommendationService.getRecommendationsForUser())
                .build();
    }

    @GetMapping("/users-for-movie/{movieId}")
    public ApiResponse<List<UserRecommendationResponse>> getUsersForMovie(@PathVariable Integer movieId) {
        return ApiResponse.<List<UserRecommendationResponse>>builder()
                .result(recommendationService.getUsersForMovie(movieId))
                .build();
    }

    @PostMapping("/update-preferences")
    public ApiResponse<Void> updatePreferences() {
        recommendationService.updateUserPreferences();
        return ApiResponse.<Void>builder()
                .message("User preferences updated successfully")
                .build();
    }

    @PostMapping("/track-click/{movieId}")
    public ApiResponse<Void> trackClick(@PathVariable Integer movieId) {
        recommendationService.trackRecommendationClick(movieId);
        return ApiResponse.<Void>builder()
                .message("Click tracked successfully")
                .build();
    }
}
