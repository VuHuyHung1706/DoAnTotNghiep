package com.web.movieservice.controller;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.GenreResponse;
import com.web.movieservice.service.genre.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public ApiResponse<List<GenreResponse>> getAllGenres() {
        return ApiResponse.<List<GenreResponse>>builder()
                .result(genreService.getAllGenres())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<GenreResponse> getGenreById(@PathVariable Integer id) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreService.getGenreById(id))
                .build();
    }

}
