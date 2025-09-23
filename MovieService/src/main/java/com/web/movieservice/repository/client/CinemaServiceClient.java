package com.web.movieservice.repository.client;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CinemaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cinema-service", url = "${cinema.service.url}")
public interface CinemaServiceClient {

    @GetMapping("/cinemas/{id}")
    ApiResponse<CinemaResponse> getCinemaById(@PathVariable Integer id);
}
