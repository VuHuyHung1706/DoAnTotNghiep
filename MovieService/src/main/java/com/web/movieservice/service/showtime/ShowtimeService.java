package com.web.movieservice.service.showtime;

import com.web.movieservice.dto.request.ShowtimeRequest;
import com.web.movieservice.dto.response.MovieWithShowtimesResponse;
import com.web.movieservice.dto.response.SeatResponse;
import com.web.movieservice.dto.response.ShowtimeResponse;
import com.web.movieservice.dto.response.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {
    List<ShowtimeResponse> getAllShowtimes();
    Page<ShowtimeResponse> getAllShowtimes(Pageable pageable);
    ShowtimeResponse getShowtimeById(Integer id);
    ShowtimeResponse createShowtime(ShowtimeRequest request);
    ShowtimeResponse updateShowtime(Integer id, ShowtimeRequest request);
    void deleteShowtime(Integer id);
    List<SeatResponse> getAvailableSeats(Integer showtimeId);
    List<ShowtimeResponse> getShowtimesByMovieId(Integer movieId);
    List<ShowtimeResponse> getShowtimesByCinemaId(Integer cinemaId);
    List<ShowtimeResponse> getShowtimesByMovieAndCinema(Integer movieId, Integer cinemaId);
    List<ShowtimeResponse> getShowtimesByMovieAndRoom(Integer movieId, Integer roomId);
    List<TicketResponse> getBookedTickets(Integer id);
    List<ShowtimeResponse> getShowtimesShowing();
    List<ShowtimeResponse> getShowtimesByDateAndRoomId(LocalDate date, Integer roomId);

    Page<ShowtimeResponse> searchShowtimesByMovieTitle(String movieTitle, Pageable pageable);

    boolean hasShowtimesByRoomId(Integer roomId);

    List<ShowtimeResponse> getShowtimesByCinemaAndDate(Integer cinemaId, LocalDate date);

    List<MovieWithShowtimesResponse> getMoviesWithShowtimesByCinemaAndDate(Integer cinemaId, LocalDate date);
}
