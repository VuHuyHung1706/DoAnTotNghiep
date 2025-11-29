package com.web.recommendationservice.controller;

import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.MovieRecommendationResponse;
import com.web.recommendationservice.dto.response.UserRecommendationResponse;
import com.web.recommendationservice.service.recommendation.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/users-for-movie/{movieId}")
    public ApiResponse<List<UserRecommendationResponse>> getUsersForMovie(@PathVariable Integer movieId) {
        return ApiResponse.<List<UserRecommendationResponse>>builder()
                .result(recommendationService.getUsersForMovie(movieId))
                .build();
    }

    @GetMapping("/movies-for-user/{username}")
    public ApiResponse<List<MovieRecommendationResponse>> getMoviesForUser(@PathVariable String username) {
        return ApiResponse.<List<MovieRecommendationResponse>>builder()
                .result(recommendationService.getMovieRecommendations(username))
                .build();
    }

    @PostMapping("/update-preferences/{username}")
    public ApiResponse<Void> updatePreferences(@PathVariable String username) {
        recommendationService.updateUserPreferencesInternal(username);
        return ApiResponse.<Void>builder()
                .message("User preferences updated successfully")
                .build();
    }

}
