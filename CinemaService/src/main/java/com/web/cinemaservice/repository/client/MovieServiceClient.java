package com.web.cinemaservice.repository.client;

import com.web.cinemaservice.dto.response.ApiResponse;
import com.web.cinemaservice.dto.response.MovieResponse;
import com.web.cinemaservice.dto.response.ShowtimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "movie-service", url = "${movie.service.url}")
public interface MovieServiceClient {

    @GetMapping("/movies/{id}")
    ApiResponse<MovieResponse> getMovieById(@PathVariable Integer id);

    @GetMapping("showtimes/movie/{movieId}")
    ApiResponse<List<ShowtimeResponse>> getShowtimesByMovieId(@PathVariable Integer movieId);

    @GetMapping("showtimes/room/{roomId}/exists")
    ApiResponse<Boolean> hasShowtimesByRoomId(@PathVariable Integer roomId);
}
