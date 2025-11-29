package com.web.recommendationservice.repository.client;

import com.web.recommendationservice.config.AuthenticationRequest;
import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.MovieResponse;
import com.web.recommendationservice.dto.response.ReviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "movie-service", url = "${movie.service.url}", configuration = AuthenticationRequest.class)
public interface MovieServiceClient {

    @GetMapping("/movies/all")
    ApiResponse<List<MovieResponse>> getAllMovies();
    
    @GetMapping("/movies/{id}")
    ApiResponse<MovieResponse> getMovieById(@PathVariable Integer id);

    @GetMapping("/reviews/user/{username}")
    ApiResponse<List<ReviewResponse>> getReviewsByUsername(@PathVariable String username);

    @GetMapping("/reviews/all")
    ApiResponse<List<ReviewResponse>> getAllReviews();

}
