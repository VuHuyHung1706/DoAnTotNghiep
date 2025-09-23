package com.web.movieservice.service.genre;


import com.web.movieservice.dto.response.GenreResponse;
import com.web.movieservice.entity.Genre;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.mapper.GenreMapper;
import com.web.movieservice.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreMapper genreMapper;

    @Override
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toGenreResponse)
                .toList();
    }

    @Override
    public GenreResponse getGenreById(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));
        return genreMapper.toGenreResponse(genre);
    }
}
