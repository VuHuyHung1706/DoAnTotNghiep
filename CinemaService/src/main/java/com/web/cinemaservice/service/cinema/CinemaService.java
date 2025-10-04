package com.web.cinemaservice.service.cinema;

import com.web.cinemaservice.dto.request.CinemaRequest;
import com.web.cinemaservice.dto.response.CinemaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CinemaService {
    List<CinemaResponse> getAllCinemas();
    Page<CinemaResponse> getAllCinemas(Pageable pageable);
    CinemaResponse getCinemaById(Integer id);
    CinemaResponse createCinema(CinemaRequest request);
    CinemaResponse updateCinema(Integer id, CinemaRequest request);
    void deleteCinema(Integer id);
    List<CinemaResponse> getCinemasByMovieId(Integer movieId);
}
