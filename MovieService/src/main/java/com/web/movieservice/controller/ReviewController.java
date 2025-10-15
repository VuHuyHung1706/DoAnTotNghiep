package com.web.movieservice.controller;

import com.web.movieservice.dto.request.ReviewRequest;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.MovieRatingResponse;
import com.web.movieservice.dto.response.ReviewResponse;
import com.web.movieservice.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.updateReview(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteReview(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewService.deleteReview(id);
        return ApiResponse.<String>builder()
                .result("Review deleted successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ReviewResponse> getReviewById(@PathVariable Integer id) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.getReviewById(id))
                .build();
    }

    @GetMapping("/movie/{movieId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByMovieId(@PathVariable Integer movieId) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getReviewsByMovieId(movieId))
                .build();
    }

    @GetMapping("/user/{username}")
    public ApiResponse<List<ReviewResponse>> getReviewsByUsername(@PathVariable String username) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getReviewsByUsername(username))
                .build();
    }

    @GetMapping("/my-reviews")
    public ApiResponse<List<ReviewResponse>> getMyReviews() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getReviewsByUsername(username))
                .build();
    }

    @GetMapping("/movie/{movieId}/rating")
    public ApiResponse<MovieRatingResponse> getMovieRating(@PathVariable Integer movieId) {
        return ApiResponse.<MovieRatingResponse>builder()
                .result(reviewService.getMovieRating(movieId))
                .build();
    }

    @GetMapping("/movie/{movieId}/my-review")
    public ApiResponse<ReviewResponse> getMyReviewForMovie(@PathVariable Integer movieId) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.getUserReviewForMovie(movieId))
                .build();
    }

}
