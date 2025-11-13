package com.web.movieservice.service.movie;

import com.web.movieservice.dto.request.MovieRequest;
import com.web.movieservice.dto.request.MovieRequestMultipart;
import com.web.movieservice.dto.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface MovieService {
    List<MovieResponse> getAllMovie();
    Page<MovieResponse> getAllMovies(Pageable pageable);
    MovieResponse getMovieById(Integer id);
    MovieResponse createMovie(MovieRequest request);
    MovieResponse createMovieWithUpload(MovieRequestMultipart request);
    MovieResponse updateMovie(Integer id, MovieRequest request);
    MovieResponse updateMovieWithUpload(Integer id, MovieRequestMultipart request);
    MovieResponse uploadPoster(Integer id, MultipartFile posterFile);
    void deleteMovie(Integer id);
    List<MovieResponse> searchMovies(String title);
    List<String> getCinemasFromMovie(String movieId);
    List<MovieResponse> searchMovies(String query, Integer cinemaId, List<Integer> genreIds, LocalDate date);
    List<MovieResponse> getMoviesByRoomId(Integer roomId);
    List<MovieResponse> getNowShowingMovies();
    List<MovieResponse> getUpcomingMovies();
    List<MovieResponse> getRecommendationMovies(String username);
    List<MovieResponse> getMoviesByCinemaId(Integer cinemaId);
}
