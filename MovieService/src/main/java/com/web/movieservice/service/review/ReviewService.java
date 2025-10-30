package com.web.movieservice.service.review;

import com.web.movieservice.dto.request.ReviewRequest;
import com.web.movieservice.dto.response.MovieRatingResponse;
import com.web.movieservice.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse updateReview(Integer reviewId, ReviewRequest request);

    void deleteReview(Integer reviewId);

    ReviewResponse getReviewById(Integer reviewId);

    List<ReviewResponse> getReviewsByMovieId(Integer movieId);

    List<ReviewResponse> getReviewsByUsername(String username);

    MovieRatingResponse getMovieRating(Integer movieId);

    ReviewResponse getUserReviewForMovie(Integer movieId);

    void createDefaultReview(String username, Integer movieId);
}
