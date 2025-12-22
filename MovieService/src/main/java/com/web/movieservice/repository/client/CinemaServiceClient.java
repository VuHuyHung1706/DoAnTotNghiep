package com.web.movieservice.repository.client;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CinemaResponse;
import com.web.movieservice.dto.response.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "cinema-service", url = "${cinema.service.url}")
public interface CinemaServiceClient {
    @GetMapping("/cinemas/{id}")
    ApiResponse<CinemaResponse> getCinemaById(@PathVariable Integer id);

    @GetMapping("/rooms/{id}")
    ApiResponse<RoomResponse> getRoomById(@PathVariable Integer id);

    @GetMapping("/rooms/cinema/{cinemaId}")
    ApiResponse<List<RoomResponse>> getRoomsByCinemaId(@PathVariable Integer cinemaId);
}
