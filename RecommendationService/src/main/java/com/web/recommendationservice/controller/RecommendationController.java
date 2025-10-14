package com.web.recommendationservice.controller;

import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.RecommendationResponse;
import com.web.recommendationservice.dto.response.UserRecommendationResponse;
import com.web.recommendationservice.entity.UserPreference;
import com.web.recommendationservice.repository.UserPreferenceRepository;
import com.web.recommendationservice.service.recommendation.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @GetMapping
    public ApiResponse<List<RecommendationResponse>> getRecommendations() {
        log.info("[v0] GET /recommendations called");
        List<RecommendationResponse> recommendations = recommendationService.getRecommendationsForUser();
        log.info("[v0] Returning {} recommendations", recommendations.size());
        return ApiResponse.<List<RecommendationResponse>>builder()
                .result(recommendations)
                .build();
    }

    @GetMapping("/users-for-movie/{movieId}")
    public ApiResponse<List<UserRecommendationResponse>> getUsersForMovie(@PathVariable Integer movieId) {
        log.info("[v0] GET /users-for-movie/{} called", movieId);
        List<UserRecommendationResponse> users = recommendationService.getUsersForMovie(movieId);
        log.info("[v0] Returning {} suitable users", users.size());
        return ApiResponse.<List<UserRecommendationResponse>>builder()
                .result(users)
                .build();
    }

    @GetMapping("/debug/preferences")
    public ApiResponse<List<UserPreference>> getMyPreferences() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[v0] GET /debug/preferences for user: {}", username);
        List<UserPreference> preferences = userPreferenceRepository.findByUsername(username);
        log.info("[v0] Found {} preferences", preferences.size());
        return ApiResponse.<List<UserPreference>>builder()
                .result(preferences)
                .build();
    }

    @PostMapping("/update-preferences")
    public ApiResponse<Void> updatePreferences() {
        log.info("[v0] POST /update-preferences called");
        recommendationService.updateUserPreferences();
        return ApiResponse.<Void>builder()
                .message("User preferences updated successfully")
                .build();
    }

    @PostMapping("/track-click/{movieId}")
    public ApiResponse<Void> trackClick(@PathVariable Integer movieId) {
        log.info("[v0] POST /track-click/{} called", movieId);
        recommendationService.trackRecommendationClick(movieId);
        return ApiResponse.<Void>builder()
                .message("Click tracked successfully")
                .build();
    }
}
