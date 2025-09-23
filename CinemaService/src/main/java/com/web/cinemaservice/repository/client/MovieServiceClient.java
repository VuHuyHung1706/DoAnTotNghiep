package com.web.cinemaservice.repository.client;

import com.web.cinemaservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service", url = "${movie.service.url}")
public interface MovieServiceClient {

    @GetMapping("/movies/{id}")
    ApiResponse<Object> getMovieById(@PathVariable Integer id);
}
