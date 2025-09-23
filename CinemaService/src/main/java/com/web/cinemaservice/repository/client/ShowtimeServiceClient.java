package com.web.cinemaservice.repository.client;

import com.web.cinemaservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "showtime-service", url = "${showtime.service.url}")
public interface ShowtimeServiceClient {

    @GetMapping("/showtimes/room/{roomId}")
    ApiResponse<List<Object>> getShowtimesByRoomId(@PathVariable Integer roomId);

    @GetMapping("/showtimes/movie/{roomId}")
    ApiResponse<List<Object>> getShowtimesByMovieId(@PathVariable Integer roomId);

}
