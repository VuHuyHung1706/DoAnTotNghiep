package com.web.movieservice.repository.client;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.ShowtimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "showtime-service", url = "${showtime.service.url}")
public interface ShowtimeServiceClient {

    @GetMapping("/showtimes/movie/{movieId}")
    ApiResponse<List<ShowtimeResponse>> getShowtimesByMovieId(@PathVariable Integer movieId);

    @GetMapping("/showtimes/cinema/{cinemaId}")
    ApiResponse<List<ShowtimeResponse>> getShowtimesByCinemaId(@PathVariable Integer cinemaId);

    @GetMapping("/showtimes/movie/{movieId}/cinema/{cinemaId}")
    ApiResponse<List<ShowtimeResponse>> getShowtimesByMovieAndCinema(
            @PathVariable Integer movieId,
            @PathVariable Integer cinemaId);

    @GetMapping("/showtimes/search-by-date")
    ApiResponse<List<ShowtimeResponse>> getShowtimesByDate(@RequestParam LocalDate date);
}
