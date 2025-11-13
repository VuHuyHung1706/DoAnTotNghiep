package com.web.movieservice.controller;

import com.web.movieservice.dto.request.ReviewRequest;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.MovieRatingResponse;
import com.web.movieservice.dto.response.ReviewResponse;
import com.web.movieservice.service.recommendation.RecommendationService;
import com.web.movieservice.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @PutMapping("/update-user-preference")
    public ApiResponse<ReviewResponse> updateUserPreference() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        recommendationService.updateUserPreferenceTask(username);
        return ApiResponse.<ReviewResponse>builder()
                .result(null)
                .build();
    }
}
