package com.web.cinemaservice.service.cinema;

import com.web.cinemaservice.dto.request.CinemaRequest;
import com.web.cinemaservice.dto.response.ApiResponse;
import com.web.cinemaservice.dto.response.CinemaResponse;
import com.web.cinemaservice.dto.response.MovieResponse;
import com.web.cinemaservice.dto.response.ShowtimeResponse;
import com.web.cinemaservice.entity.Cinema;
import com.web.cinemaservice.exception.AppException;
import com.web.cinemaservice.exception.ErrorCode;
import com.web.cinemaservice.mapper.CinemaMapper;
import com.web.cinemaservice.repository.CinemaRepository;
import com.web.cinemaservice.repository.client.MovieServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private MovieServiceClient movieServiceClient;

//    @Autowired
//    private MovieRepository movieRepository;

//    @Autowired
//    private ShowtimeRepository showtimeRepository;

    @Autowired
    private CinemaMapper cinemaMapper;

    @Override
    public List<CinemaResponse> getAllCinemas() {
        return cinemaRepository.findAll()
                .stream()
                .map(cinemaMapper::toCinemaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CinemaResponse> getAllCinemas(Pageable pageable) {
        return cinemaRepository.findAll(pageable)
                .map(cinemaMapper::toCinemaResponse);
    }

    @Override
    public CinemaResponse getCinemaById(Integer id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_EXISTED));
        return cinemaMapper.toCinemaResponse(cinema);
    }

    @Override
    public CinemaResponse createCinema(CinemaRequest request) {
        Cinema cinema = cinemaMapper.toCinema(request);
        cinema = cinemaRepository.save(cinema);
        return cinemaMapper.toCinemaResponse(cinema);
    }

    @Override
    public CinemaResponse updateCinema(Integer id, CinemaRequest request) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_EXISTED));

        cinemaMapper.updateCinema(cinema, request);
        cinema = cinemaRepository.save(cinema);
        return cinemaMapper.toCinemaResponse(cinema);
    }

    @Override
    public void deleteCinema(Integer id) {
        if (!cinemaRepository.existsById(id)) {
            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
        }

        Cinema cinema = cinemaRepository.findById(id).get();
        if (!cinema.getRooms().isEmpty()) {
            throw new AppException(ErrorCode.CANNOT_DELETE_CINEMA_HAS_ROOMS);
        }

        cinemaRepository.deleteById(id);
    }

    @Override
    public List<CinemaResponse> getCinemasByMovieId(Integer movieId) {

//        if (!movieRepository.existsById(movieId)) {
//            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
//        }

        ApiResponse<MovieResponse> movieApiResponse = movieServiceClient.getMovieById(movieId);
        if (movieApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(movieApiResponse.getMessage()));
        }

//        List<Showtime> showtimes = showtimeRepository.findByMovieId(movieId);

        ApiResponse<List<ShowtimeResponse>> showtimesApiResponse = movieServiceClient.getShowtimesByMovieId(movieId);

        if (showtimesApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(showtimesApiResponse.getMessage()));
        }

        List<ShowtimeResponse> showtimes = showtimesApiResponse.getResult();
//        Set<Integer> cinemaIds = showtimes.stream()
//                .map(showtime -> showtime.getRoom().getCinemaId())
//                .collect(Collectors.toSet());
        List<Integer> roomIds = showtimes.stream()
                .map(ShowtimeResponse::getRoomId)
                .toList();

        // Get cinemas
        List<Cinema> cinemas = cinemaRepository.findDistinctByRooms_IdIn(roomIds);

        return cinemas.stream()
                .map(cinemaMapper::toCinemaResponse)
                .collect(Collectors.toList());
    }
}
