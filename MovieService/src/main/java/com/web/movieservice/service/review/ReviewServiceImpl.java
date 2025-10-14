package com.web.movieservice.service.review;

import com.web.movieservice.dto.request.ReviewRequest;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CustomerResponse;
import com.web.movieservice.dto.response.MovieRatingResponse;
import com.web.movieservice.dto.response.ReviewResponse;
import com.web.movieservice.entity.Review;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.mapper.ReviewMapper;
import com.web.movieservice.repository.MovieRepository;
import com.web.movieservice.repository.ReviewRepository;
import com.web.movieservice.repository.client.IdentityServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private IdentityServiceClient identityServiceClient;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Check if movie exists
        if (!movieRepository.existsById(request.getMovieId())) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        // Check if user already reviewed this movie
        if (reviewRepository.existsByMovieIdAndUsername(request.getMovieId(), username)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = reviewMapper.toReview(request);
        review.setUsername(username);

        review = reviewRepository.save(review);

//        return mapReviewToResponse(review);
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Integer reviewId, ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        // Check if the review belongs to the user
        if (!review.getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewMapper.updateReview(review, request);
        review = reviewRepository.save(review);

    return reviewMapper.toReviewResponse(review);
    }

    @Override
    public void deleteReview(Integer reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        // Check if the review belongs to the user
        if (!review.getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public ReviewResponse getReviewById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

    return reviewMapper.toReviewResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByMovieId(Integer movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        List<Review> reviews = reviewRepository.findByMovieId(movieId);

        return reviews.stream()
                .map(reviewMapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUsername(String username) {
        List<Review> reviews = reviewRepository.findByUsername(username);

        return reviews.stream()
                .map(reviewMapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MovieRatingResponse getMovieRating(Integer movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        Double averageRating = reviewRepository.getAverageRatingByMovieId(movieId);
        Long reviewCount = reviewRepository.getReviewCountByMovieId(movieId);

        return MovieRatingResponse.builder()
                .movieId(movieId)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .reviewCount(reviewCount != null ? reviewCount.intValue() : 0)
                .build();
    }

    @Override
    public ReviewResponse getUserReviewForMovie(Integer movieId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Review review = reviewRepository.findByMovieIdAndUsername(movieId, username)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        return reviewMapper.toReviewResponse(review);
    }
}
