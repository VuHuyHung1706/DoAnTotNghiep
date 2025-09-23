package com.web.showtimeservice.repository.client;

import com.web.showtimeservice.dto.response.ApiResponse;
import com.web.showtimeservice.dto.response.MovieResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service", url = "${movie.service.url}")
public interface MovieServiceClient {

    @GetMapping("/movies/{id}")
    ApiResponse<MovieResponse> getMovieById(@PathVariable Integer id);
}
