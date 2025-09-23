package com.web.movieservice.service.movie;

import com.web.movieservice.dto.request.MovieRequest;
import com.web.movieservice.dto.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MovieService {
    List<MovieResponse> getAllMovie();
    Page<MovieResponse> getAllMovies(Pageable pageable);
    MovieResponse getMovieById(Integer id);
    MovieResponse createMovie(MovieRequest request);
    MovieResponse updateMovie(Integer id, MovieRequest request);
    void deleteMovie(Integer id);
    List<MovieResponse> searchMovies(String title);
//    List<MovieResponse> searchMovies(String query, Integer cinemaId, List<Integer> genreIds, LocalDate date);
//    List<MovieResponse> getMoviesByRoomId(Integer roomId);
//    List<MovieResponse> getNowShowingMovies();
//    List<MovieResponse> getUpcomingMovies();
//    List<MovieResponse> getMoviesByCinemaId(Integer cinemaId);
}
