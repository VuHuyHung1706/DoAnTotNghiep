package com.web.movieservice.service.genre;


import com.web.movieservice.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {
    List<GenreResponse> getAllGenres();
    GenreResponse getGenreById(Integer id);
}
