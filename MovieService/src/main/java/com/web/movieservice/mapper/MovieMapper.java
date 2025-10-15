package com.web.movieservice.mapper;

import com.web.movieservice.dto.request.MovieRequest;
import com.web.movieservice.dto.response.MovieResponse;
import com.web.movieservice.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "actors", ignore = true)
    Movie toMovie(MovieRequest request);

    @Mapping(target = "nowShowing", ignore = true)
    @Mapping(target = "upcoming", ignore = true)
    MovieResponse toMovieResponse(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "actors", ignore = true)
    void updateMovie(@MappingTarget Movie movie, MovieRequest request);
}
