package com.web.movieservice.service.movie;

import com.web.movieservice.dto.request.MovieRequest;
import com.web.movieservice.dto.response.*;
import com.web.movieservice.entity.Actor;
import com.web.movieservice.entity.Genre;
import com.web.movieservice.entity.Movie;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.mapper.MovieMapper;
import com.web.movieservice.repository.ActorRepository;
import com.web.movieservice.repository.GenreRepository;
import com.web.movieservice.repository.MovieRepository;
import com.web.movieservice.repository.ShowtimeRepository;
import com.web.movieservice.repository.client.CinemaServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Autowired
    private MovieMapper movieMapper;

    @Override
    public List<MovieResponse> getAllMovie() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(movieMapper::toMovieResponse);
    }

    @Override
    public MovieResponse getMovieById(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = movieMapper.toMovie(request);

        // Set genres
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>();
            for (Integer genreId : request.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));
                genres.add(genre);
            }
            movie.setGenres(genres);
        }

        // Set actors
        if (request.getActorIds() != null && !request.getActorIds().isEmpty()) {
            Set<Actor> actors = new HashSet<>();
            for (Integer actorId : request.getActorIds()) {
                Actor actor = actorRepository.findById(actorId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACTOR_NOT_EXISTED));
                actors.add(actor);
            }
            movie.setActors(actors);
        }

        movie = movieRepository.save(movie);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public MovieResponse updateMovie(Integer id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        movieMapper.updateMovie(movie, request);

        // Update genres
        if (request.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>();
            for (Integer genreId : request.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));
                genres.add(genre);
            }
            movie.setGenres(genres);
        }

        // Update actors
        if (request.getActorIds() != null) {
            Set<Actor> actors = new HashSet<>();
            for (Integer actorId : request.getActorIds()) {
                Actor actor = actorRepository.findById(actorId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACTOR_NOT_EXISTED));
                actors.add(actor);
            }
            movie.setActors(actors);
        }

        movie = movieRepository.save(movie);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public void deleteMovie(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }
        movieRepository.deleteById(id);
    }

    @Override
    public List<MovieResponse> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(movieMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getCinemasFromMovie(String movieId) {
        return List.of();
    }

    @Override
    public List<MovieResponse> searchMovies(String query, Integer cinemaId, List<Integer> genreIds, LocalDate date) {
        List<Movie> movies = movieRepository.findAll();

        // Filter by title or actor name
        if (query != null && !query.isEmpty()) {
            Set<Movie> filteredByQuery = new HashSet<>();

            filteredByQuery.addAll(movieRepository.findByTitleContainingIgnoreCase(query));

            List<Actor> matchingActors = actorRepository.findAll().stream()
                    .filter(actor -> (actor.getFirstName() != null && actor.getFirstName().toLowerCase().contains(query.toLowerCase())) ||
                            (actor.getLastName() != null && actor.getLastName().toLowerCase().contains(query.toLowerCase())))
                    .collect(Collectors.toList());

            for (Actor actor : matchingActors) {
                filteredByQuery.addAll(actor.getMovies());
            }
            movies = movies.stream()
                    .filter(filteredByQuery::contains)
                    .collect(Collectors.toList());
        }

        // Filter by cinemaId
//        if (cinemaId != null) {
//            if (!cinemaRepository.existsById(cinemaId)) {
//                throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
//            }
//            List<Showtime> showtimesAtCinema = showtimeRepository.findByRoomCinemaId(cinemaId);
//            Set<Integer> movieIdsAtCinema = showtimesAtCinema.stream()
//                    .map(Showtime::getMovieId)
//                    .collect(Collectors.toSet());
//
//            movies = movies.stream()
//                    .filter(movie -> movieIdsAtCinema.contains(movie.getId()))
//                    .collect(Collectors.toList());
//        }

        // Filter by genreIds
        if (genreIds != null && !genreIds.isEmpty()) {
            Set<Integer> genreIdSet = new HashSet<>(genreIds);
            movies = movies.stream()
                    .filter(movie -> movie.getGenres().stream()
                            .anyMatch(genre -> genreIdSet.contains(genre.getId())))
                    .collect(Collectors.toList());
        }

        // --- Filter by date
        if (date != null) {
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to = date.plusDays(1).atStartOfDay().minusSeconds(1);

            Set<Integer> movieIdsOnDate = showtimeRepository.findAll().stream()
                    .filter(showtime -> !showtime.getStartTime().isBefore(from) && !showtime.getStartTime().isAfter(to))
                    .map(showtime -> showtime.getMovie().getId())
                    .collect(Collectors.toSet());

            movies = movies.stream()
                    .filter(movie -> movieIdsOnDate.contains(movie.getId()))
                    .collect(Collectors.toList());
        }

        return movies.stream()
                .map(this::mapMovieToResponseWithStatus)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<MovieResponse> getMoviesByRoomId(Integer roomId) {
//        LocalDateTime now = LocalDateTime.now();
//
//        if (!roomRepository.existsById(roomId)) {
//            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
//        }
//
//        List<Movie> movies = showtimeRepository.findByRoomId(roomId)
//                .stream().distinct()
//                .filter(showtime -> showtime.getStartTime().isAfter(now) || showtime.getEndTime().isAfter(now))
//                .map(Showtime::getMovie)
//                .toList();
//
//
//        return movies.stream()
//                .map(this::mapMovieToResponseWithStatus)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<MovieResponse> getNowShowingMovies() {
//        LocalDateTime now = LocalDateTime.now();
//
//        List<Movie> nowShowingMovies = showtimeRepository.findAll()
//                .stream()
//                .filter(showtime -> (showtime.getStartTime().isBefore(now) || showtime.getStartTime().isEqual(now))
//                        && (showtime.getEndTime().isAfter(now) || showtime.getEndTime().isEqual(now)))
//                .map(Showtime::getMovie)
//                .distinct()
//                .collect(Collectors.toList());
//
//        return nowShowingMovies.stream()
//                .map(movieMapper::toMovieResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<MovieResponse> getUpcomingMovies() {
//        LocalDateTime now = LocalDateTime.now();
//
//        List<Movie> upcomingMovies = showtimeRepository.findAll()
//                .stream()
//                .filter(showtime -> showtime.getStartTime().isAfter(now))
//                .map(Showtime::getMovie)
//                .distinct()
//                .collect(Collectors.toList());
//
//        return upcomingMovies.stream()
//                .map(movieMapper::toMovieResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<MovieResponse> getMoviesByCinemaId(Integer cinemaId) {
//        if (!cinemaRepository.existsById(cinemaId)) {
//            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//
//        List<Showtime> showtimes = showtimeRepository.findByRoomCinemaId(cinemaId);
//
//        List<Movie> movies = showtimes.stream()
//                .filter(showtime -> showtime.getStartTime().isAfter(now))
//                .map(Showtime::getMovie)
//                .distinct()
//                .collect(Collectors.toList());
//
//        return movies.stream()
//                .map(movieMapper::toMovieResponse)
//                .collect(Collectors.toList());
//    }
//
//
    public MovieResponse mapMovieToResponseWithStatus(Movie movie) {
        MovieResponse response = movieMapper.toMovieResponse(movie);
        LocalDateTime now = LocalDateTime.now();

        boolean nowShowing = movie.getShowtimes().stream()
                .anyMatch(showtime ->
                        (showtime.getStartTime().isBefore(now) || showtime.getStartTime().isEqual(now)) &&
                                (showtime.getEndTime().isAfter(now) || showtime.getEndTime().isEqual(now))
                );

        boolean upcoming = movie.getShowtimes().stream()
                .anyMatch(showtime -> showtime.getStartTime().isAfter(now));

        response.setNowShowing(nowShowing);
        response.setUpcoming(upcoming);
        return response;
    }
}
