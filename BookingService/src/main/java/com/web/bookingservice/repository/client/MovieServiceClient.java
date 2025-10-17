package com.web.bookingservice.repository.client;

import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.ShowtimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service", url = "${movie.service.url}")
public interface MovieServiceClient {
    @GetMapping("/showtimes/{id}")
    ApiResponse<ShowtimeResponse> getShowtimeById(@PathVariable Integer id);
}
