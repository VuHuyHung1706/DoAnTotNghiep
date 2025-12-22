package com.web.movieservice.service.movie;

import com.web.movieservice.dto.request.MovieRequest;
import com.web.movieservice.dto.request.MovieRequestMultipart;
import com.web.movieservice.dto.response.*;
import com.web.movieservice.entity.Actor;
import com.web.movieservice.entity.Genre;
import com.web.movieservice.entity.Movie;
import com.web.movieservice.entity.Showtime;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.mapper.MovieMapper;
import com.web.movieservice.repository.ActorRepository;
import com.web.movieservice.repository.GenreRepository;
import com.web.movieservice.repository.MovieRepository;
import com.web.movieservice.repository.ShowtimeRepository;
import com.web.movieservice.repository.client.CinemaServiceClient;
import com.web.movieservice.repository.client.RecommendationServiceClient;
import com.web.movieservice.service.review.ReviewService;
import com.web.movieservice.service.file.FileUploadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private RecommendationServiceClient recommendationServiceClient;
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public List<MovieResponse> getAllMovie() {
        return movieRepository.findAll()
                .stream()
                .map(this::mapMovieToResponseWithStatus)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(this::mapMovieToResponseWithStatus);
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
    public MovieResponse createMovieWithUpload(MovieRequestMultipart request) {
        // Convert multipart request to regular movie request
        MovieRequest movieRequest = MovieRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .language(request.getLanguage())
                .trailer(request.getTrailer())
                .releaseDate(request.getReleaseDate())
                .genreIds(request.getGenreIds())
                .actorIds(request.getActorIds())
                .build();

        // Upload poster file if provided
        if (request.getPosterFile() != null && !request.getPosterFile().isEmpty()) {
            String posterUrl = fileUploadService.uploadFile(request.getPosterFile());
            movieRequest.setPoster(posterUrl);
        }

        return createMovie(movieRequest);
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
    public MovieResponse updateMovieWithUpload(Integer id, MovieRequestMultipart request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        // Upload new poster if provided and delete old one
        if (request.getPosterFile() != null && !request.getPosterFile().isEmpty()) {
            // Delete old poster if exists
            if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
                fileUploadService.deleteFile(movie.getPoster());
            }

            // Upload new poster
            String newPosterUrl = fileUploadService.uploadFile(request.getPosterFile());
            movie.setPoster(newPosterUrl);
        }

        // Update other fields
        MovieRequest movieRequest = MovieRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .language(request.getLanguage())
                .poster(movie.getPoster()) // Keep existing or new poster URL
                .trailer(request.getTrailer())
                .releaseDate(request.getReleaseDate())
                .genreIds(request.getGenreIds())
                .actorIds(request.getActorIds())
                .build();

        return updateMovie(id, movieRequest);
    }

    @Override
    public MovieResponse uploadPoster(Integer id, MultipartFile posterFile) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        // Delete old poster if exists
        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            fileUploadService.deleteFile(movie.getPoster());
        }

        // Upload new poster
        String newPosterUrl = fileUploadService.uploadFile(posterFile);
        movie.setPoster(newPosterUrl);

        movie = movieRepository.save(movie);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public void deleteMovie(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        List<Showtime> showtimes = showtimeRepository.findByMovieId(id);
        if (!showtimes.isEmpty()) {
            throw new AppException(ErrorCode.CANNOT_DELETE_MOVIE_HAS_SHOWTIMES);
        }

        if (!movie.getReviews().isEmpty()) {
            throw new AppException(ErrorCode.CANNOT_DELETE_MOVIE_HAS_REVIEWS);
        }

        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            fileUploadService.deleteFile(movie.getPoster());
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
        if (cinemaId != null) {
            // Check cinema exists
            ApiResponse<CinemaResponse> cinemaApiResponse = cinemaServiceClient.getCinemaById(cinemaId);
            if (cinemaApiResponse.getCode() != 1000) {
                throw new AppException(ErrorCode.fromMessage(cinemaApiResponse.getMessage()));
            }

            List<Integer> roomIds = cinemaApiResponse.getResult().getRooms().stream().map(RoomResponse::getId).collect(Collectors.toList());
            List<Showtime> showtimesAtCinema = showtimeRepository.findByRoomIdIn(roomIds);
            Set<Integer> movieIdsAtCinema = showtimesAtCinema.stream()
                    .map(Showtime::getMovieId)
                    .collect(Collectors.toSet());

            movies = movies.stream()
                    .filter(movie -> movieIdsAtCinema.contains(movie.getId()))
                    .collect(Collectors.toList());
        }

        // Filter by genreIds
        if (genreIds != null && !genreIds.isEmpty()) {
            Set<Integer> genreIdSet = new HashSet<>(genreIds);
            movies = movies.stream()
                    .filter(movie -> movie.getGenres().stream()
                            .anyMatch(genre -> genreIdSet.contains(genre.getId())))
                    .collect(Collectors.toList());
        }

        // Filter by date
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

    @Override
    public List<MovieResponse> getMoviesByRoomId(Integer roomId) {
        LocalDateTime now = LocalDateTime.now();

        ApiResponse<RoomResponse> roomApiResponse = cinemaServiceClient.getRoomById(roomId);
        if (roomApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(roomApiResponse.getMessage()));
        }

        List<Movie> movies = showtimeRepository.findByRoomId(roomId)
                .stream().distinct()
                .filter(showtime -> showtime.getStartTime().isAfter(now) || showtime.getEndTime().isAfter(now))
                .map(Showtime::getMovie)
                .toList();

        return movies.stream()
                .map(this::mapMovieToResponseWithStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getNowShowingMovies() {
        LocalDateTime now = LocalDateTime.now();

        List<Movie> nowShowingMovies = showtimeRepository.findAll()
                .stream()
                .filter(showtime -> showtime.getStartTime().isAfter(now))
                .map(Showtime::getMovie)
                .distinct()
                .collect(Collectors.toList());

        return nowShowingMovies.stream()
                .map(movieMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getUpcomingMovies() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        List<Movie> allMovies = movieRepository.findAll();
//        List<Integer> moviesWithShowtimes = showtimeRepository.findAll()
//                .stream()
//                .map(showtime -> showtime.getMovie().getId())
//                .distinct()
//                .collect(Collectors.toList());

        List<Movie> upcomingMovies = allMovies.stream()
//                .filter(movie -> !moviesWithShowtimes.contains(movie.getId()))
                .filter(movie -> movie.getReleaseDate() != null && movie.getReleaseDate().isAfter(today))
                .collect(Collectors.toList());

        return upcomingMovies.stream()
                .map(movieMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getRecommendationMovies(String username) {
        List<Integer> recommendationMovieIds = new ArrayList<>();
        try {
            ApiResponse<List<MovieRecommendationResponse>> recommendationsResponse = recommendationServiceClient.getMoviesForUser(username);
            List<MovieRecommendationResponse> recommendations = recommendationsResponse.getResult();
            recommendationMovieIds = recommendations.stream().map(MovieRecommendationResponse::getMovieId).toList();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        List<Movie> movies = movieRepository.findByIdIn(recommendationMovieIds);

        return movies.stream()
                .map(movie -> {
                    MovieResponse movieResponse = movieMapper.toMovieResponse(movie);
                    movieResponse.setRating(reviewService.getMovieRating(movie.getId()).getAverageRating());
                    return movieResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getMoviesByCinemaId(Integer cinemaId) {
        ApiResponse<CinemaResponse> cinemaApiResponse = cinemaServiceClient.getCinemaById(cinemaId);
        if (cinemaApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(cinemaApiResponse.getMessage()));
        }

        LocalDateTime now = LocalDateTime.now();

        CinemaResponse cinemaResponse = cinemaApiResponse.getResult();
        List<Integer> roomIds = cinemaResponse.getRooms().stream().map(RoomResponse::getId).collect(Collectors.toList());
        List<Showtime> showtimes = showtimeRepository.findByRoomIdIn(roomIds);

        List<Movie> movies = showtimes.stream()
                .filter(showtime -> showtime.getStartTime().isAfter(now))
                .map(Showtime::getMovie)
                .distinct()
                .collect(Collectors.toList());

        return movies.stream()
                .map(movieMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    public MovieResponse mapMovieToResponseWithStatus(Movie movie) {
        MovieResponse response = movieMapper.toMovieResponse(movie);
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        boolean nowShowing = movie.getShowtimes().stream()
                .anyMatch(showtime -> showtime.getStartTime().isAfter(now));

        boolean hasShowtimes = !movie.getShowtimes().isEmpty();
        boolean upcoming = !hasShowtimes && movie.getReleaseDate() != null && movie.getReleaseDate().isAfter(today);

        response.setNowShowing(nowShowing);
        response.setUpcoming(upcoming);
        return response;
    }
}
