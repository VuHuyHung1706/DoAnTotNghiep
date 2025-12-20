package com.web.movieservice.controller;

import com.web.movieservice.dto.request.ShowtimeRequest;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.SeatResponse;
import com.web.movieservice.dto.response.ShowtimeResponse;
import com.web.movieservice.dto.response.TicketResponse;
import com.web.movieservice.service.showtime.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping("/all")
    public ApiResponse<List<ShowtimeResponse>> getAllShowtimes() {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.getAllShowtimes())
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ShowtimeResponse>> getAllShowtimes(Pageable pageable) {
        return ApiResponse.<Page<ShowtimeResponse>>builder()
                .result(showtimeService.getAllShowtimes(pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ShowtimeResponse> getShowtimeById(@PathVariable Integer id) {
        return ApiResponse.<ShowtimeResponse>builder()
                .result(showtimeService.getShowtimeById(id))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ApiResponse<ShowtimeResponse> createShowtime(@Valid @RequestBody ShowtimeRequest request) {
        return ApiResponse.<ShowtimeResponse>builder()
                .result(showtimeService.createShowtime(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ApiResponse<ShowtimeResponse> updateShowtime(@PathVariable Integer id,
                                                        @Valid @RequestBody ShowtimeRequest request) {
        return ApiResponse.<ShowtimeResponse>builder()
                .result(showtimeService.updateShowtime(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ApiResponse<String> deleteShowtime(@PathVariable Integer id) {
        showtimeService.deleteShowtime(id);
        return ApiResponse.<String>builder()
                .result("Showtime deleted successfully")
                .build();
    }

    @GetMapping("/{id}/available-seats")
    public ApiResponse<List<SeatResponse>> getAvailableSeats(@PathVariable Integer id) {
        return ApiResponse.<List<SeatResponse>>builder()
                .result(showtimeService.getAvailableSeats(id))
                .build();
    }

    @GetMapping("/movie/{movieId}")
    public ApiResponse<List<ShowtimeResponse>> getShowtimesByMovieId(@PathVariable Integer movieId) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.getShowtimesByMovieId(movieId))
                .build();
    }

    @GetMapping("/cinema/{cinemaId}")
    public ApiResponse<List<ShowtimeResponse>> getShowtimesByCinemaId(@PathVariable Integer cinemaId) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.getShowtimesByCinemaId(cinemaId))
                .build();
    }

    @GetMapping("/movie/{movieId}/cinema/{cinemaId}")
    public ApiResponse<List<ShowtimeResponse>> getShowtimesByMovieAndCinema(
            @PathVariable Integer movieId,
            @PathVariable Integer cinemaId) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.getShowtimesByMovieAndCinema(movieId, cinemaId))
                .build();
    }

    @GetMapping("/movie/{movieId}/room/{roomId}")
    public ApiResponse<List<ShowtimeResponse>> getShowtimesByMovieAndRoom(
            @PathVariable Integer movieId,
            @PathVariable Integer roomId) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.getShowtimesByMovieAndRoom(movieId, roomId))
                .build();
    }

    @GetMapping("/{id}/booked-tickets")
    public ApiResponse<List<TicketResponse>> getBookedTickets(@PathVariable Integer id) {
        return ApiResponse.<List<TicketResponse>>builder()
                .result(showtimeService.getBookedTickets(id))
                .build();
    }

    @GetMapping("/room/{roomId}/exists")
    public ApiResponse<Boolean> hasShowtimesByRoomId(@PathVariable Integer roomId) {
        boolean exists = showtimeService.hasShowtimesByRoomId(roomId);
        return ApiResponse.<Boolean>builder()
                .code(1000)
                .result(exists)
                .build();
    }

    @PostMapping("/search-by-date-and-room")
    public ApiResponse<List<ShowtimeResponse>> searchShowtimesByDateAndRoom(
            @RequestBody ShowtimeRequest request
    ) {
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .result(showtimeService.getShowtimesByDateAndRoomId(request.getStartTime().toLocalDate(), request.getRoomId()))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<Page<ShowtimeResponse>> searchShowtimesByMovieTitle(
            @RequestParam String movieTitle,
            Pageable pageable
    ) {
        return ApiResponse.<Page<ShowtimeResponse>>builder()
                .result(showtimeService.searchShowtimesByMovieTitle(movieTitle, pageable))
                .build();
    }
}
