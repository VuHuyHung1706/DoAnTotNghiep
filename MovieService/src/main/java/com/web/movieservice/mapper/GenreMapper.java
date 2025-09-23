package com.web.movieservice.mapper;


import com.web.movieservice.dto.response.GenreResponse;
import com.web.movieservice.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponse toGenreResponse(Genre genre);
}
