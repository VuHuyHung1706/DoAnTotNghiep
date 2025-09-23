package com.web.movieservice.controller;

import com.web.movieservice.dto.request.MovieRequest;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.MovieResponse;
import com.web.movieservice.service.movie.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/all")
    public ApiResponse<List<MovieResponse>> getAllMovies() {
        return ApiResponse.<List<MovieResponse>>builder()
                .result(movieService.getAllMovie())
                .build();
    }

    @GetMapping
    public ApiResponse<Page<MovieResponse>> getAllMovies(Pageable pageable) {
        return ApiResponse.<Page<MovieResponse>>builder()
                .result(movieService.getAllMovies(pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getMovieById(@PathVariable Integer id) {
        return ApiResponse.<MovieResponse>builder()
                .result(movieService.getMovieById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return ApiResponse.<MovieResponse>builder()
                .result(movieService.createMovie(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> updateMovie(@PathVariable Integer id, @Valid @RequestBody MovieRequest request) {
        return ApiResponse.<MovieResponse>builder()
                .result(movieService.updateMovie(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMovie(@PathVariable Integer id) {
        movieService.deleteMovie(id);
        return ApiResponse.<String>builder()
                .result("Movie deleted successfully")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<MovieResponse>> searchMovies(@RequestParam String title) {
        return ApiResponse.<List<MovieResponse>>builder()
                .result(movieService.searchMovies(title))
                .build();
    }

//    @GetMapping("/search-advanced")
//    public ApiResponse<List<MovieResponse>> searchMovieAdvanced(
//            @RequestParam(required = false) String query,
//            @RequestParam(required = false) Integer cinemaId,
//            @RequestParam(required = false) List<Integer> genreIds,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
//    ) {
//        return ApiResponse.<List<MovieResponse>>builder()
//                .result(movieService.searchMovies(query, cinemaId, genreIds, date))
//                .build();
//    }
//
//
//    @GetMapping("/room/{roomId}")
//    public ApiResponse<List<MovieResponse>> getMoviesByRoomId(@PathVariable Integer roomId) {
//        return ApiResponse.<List<MovieResponse>>builder()
//                .result(movieService.getMoviesByRoomId(roomId))
//                .build();
//    }
//
//    @GetMapping("/now-showing")
//    public ApiResponse<List<MovieResponse>> getNowShowingMovies() {
//        return ApiResponse.<List<MovieResponse>>builder()
//                .result(movieService.getNowShowingMovies())
//                .build();
//    }
//
//    @GetMapping("/upcoming")
//    public ApiResponse<List<MovieResponse>> getUpcomingMovies() {
//        return ApiResponse.<List<MovieResponse>>builder()
//                .result(movieService.getUpcomingMovies())
//                .build();
//    }
}
