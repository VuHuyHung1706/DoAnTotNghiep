package com.web.movieservice.repository.client;

import com.web.movieservice.config.AuthenticationRequest;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CustomerResponse;
import com.web.movieservice.dto.response.MovieRecommendationResponse;
import com.web.movieservice.dto.response.UserRecommendationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "recommendation-service", url = "${recommendation.service.url}")
public interface RecommendationServiceClient {

    @GetMapping("recommendations/users-for-movie/{movieId}")
    ApiResponse<List<UserRecommendationResponse>> getUsersForMovie(@PathVariable Integer movieId);

    @GetMapping("recommendations/movies-for-user/{username}")
    ApiResponse<List<MovieRecommendationResponse>> getMoviesForUser(@PathVariable String username);

    @PostMapping("/recommendations/update-preferences/{username}")
    public ApiResponse<Void> updatePreferences(@PathVariable String username);

}
