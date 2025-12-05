package com.web.cinemaservice.controller;

import com.web.cinemaservice.dto.request.CinemaRequest;
import com.web.cinemaservice.dto.response.ApiResponse;
import com.web.cinemaservice.dto.response.CinemaResponse;
import com.web.cinemaservice.service.cinema.CinemaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cinemas")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    @GetMapping("/all")
    public ApiResponse<List<CinemaResponse>> getAllCinemas() {
        return ApiResponse.<List<CinemaResponse>>builder()
                .result(cinemaService.getAllCinemas())
                .build();
    }

    @GetMapping
    public ApiResponse<Page<CinemaResponse>> getAllCinemas(Pageable pageable) {
        return ApiResponse.<Page<CinemaResponse>>builder()
                .result(cinemaService.getAllCinemas(pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CinemaResponse> getCinemaById(@PathVariable Integer id) {
        return ApiResponse.<CinemaResponse>builder()
                .result(cinemaService.getCinemaById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<CinemaResponse> createCinema(@Valid @RequestBody CinemaRequest request) {
        return ApiResponse.<CinemaResponse>builder()
                .result(cinemaService.createCinema(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CinemaResponse> updateCinema(@PathVariable Integer id, @Valid @RequestBody CinemaRequest request) {
        return ApiResponse.<CinemaResponse>builder()
                .result(cinemaService.updateCinema(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCinema(@PathVariable Integer id) {
        cinemaService.deleteCinema(id);
        return ApiResponse.<String>builder()
                .result("Cinema deleted successfully")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<CinemaResponse>> searchCinemas(@RequestParam String name) {
        return ApiResponse.<List<CinemaResponse>>builder()
                .result(cinemaService.searchCinemasByName(name))
                .build();
    }

//    @GetMapping("/movie/{movieId}")
//    public ApiResponse<List<CinemaResponse>> getCinemasByMovieId(@PathVariable Integer movieId) {
//        return ApiResponse.<List<CinemaResponse>>builder()
//                .result(cinemaService.getCinemasByMovieId(movieId))
//                .build();
//    }
}
