package com.web.showtimeservice.service;

import com.web.showtimeservice.dto.request.ShowtimeRequest;
import com.web.showtimeservice.dto.response.ApiResponse;
import com.web.showtimeservice.dto.response.MovieResponse;
import com.web.showtimeservice.dto.response.SeatResponse;
import com.web.showtimeservice.dto.response.ShowtimeResponse;
import com.web.showtimeservice.entity.Showtime;
import com.web.showtimeservice.exception.AppException;
import com.web.showtimeservice.exception.ErrorCode;
import com.web.showtimeservice.mapper.ShowtimeMapper;
import com.web.showtimeservice.repository.ShowtimeRepository;
import com.web.showtimeservice.repository.client.MovieServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShowtimeServiceImpl implements ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieServiceClient movieServiceClient;

//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private TicketRepository ticketRepository;
//
//    @Autowired
//    private CinemaRepository cinemaRepository;

    @Autowired
    private ShowtimeMapper showtimeMapper;
//
//    @Autowired
//    private SeatMapper seatMapper;
//
//    @Autowired
//    private TicketMapper ticketMapper;

    @Override
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(showtimeMapper::toShowtimeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShowtimeResponse> getAllShowtimes(Pageable pageable) {
        return showtimeRepository.findAll(pageable)
                .map(showtimeMapper::toShowtimeResponse);
    }

    @Override
    public ShowtimeResponse getShowtimeById(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));
        return showtimeMapper.toShowtimeResponse(showtime);
    }

//    @Override
//    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
//        Movie movie = movieRepository.findById(request.getMovieId())
//                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));
//
//        Room room = roomRepository.findById(request.getRoomId())
//                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
//
//            // Calculate end time based on movie duration
//        LocalDateTime endTimeOnMovie = request.getStartTime().plusMinutes(movie.getDuration());
//
//        if (request.getEndTime() == null) {
//            request.setEndTime(endTimeOnMovie);
//        }
//        else if (request.getEndTime().isBefore(endTimeOnMovie)) {
//            throw new AppException(ErrorCode.INVALID_KEY);
//        }
//
//        // Check for conflicting showtimes in the same room
//        List<Showtime> conflictingShowtimes = showtimeRepository.findConflictingShowtimes(
//                request.getRoomId(), request.getStartTime(), request.getEndTime());
//
//        if (!conflictingShowtimes.isEmpty()) {
//            throw new AppException(ErrorCode.INVALID_KEY); // You might want to create a specific error for this
//        }
//
//        Showtime showtime = showtimeMapper.toShowtime(request);
//        showtime.setEndTime(request.getEndTime());
//
//        showtime = showtimeRepository.save(showtime);
//        return showtimeMapper.toShowtimeResponse(showtime);
//    }
//
//    @Override
//    public ShowtimeResponse updateShowtime(Integer id, ShowtimeRequest request) {
//        Showtime showtime = showtimeRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));
//
//        // Validate movie exists
//        Movie movie = movieRepository.findById(request.getMovieId())
//                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));
//
//        // Validate room exists
//        if (!roomRepository.existsById(request.getRoomId())) {
//            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
//        }
//
//        // Calculate end time based on movie duration
//        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());
//
//        // Check for conflicting showtimes in the same room (excluding current showtime)
//        List<Showtime> conflictingShowtimes = showtimeRepository.findConflictingShowtimes(
//                request.getRoomId(), request.getStartTime(), endTime);
//
//        conflictingShowtimes = conflictingShowtimes.stream()
//                .filter(s -> !s.getId().equals(id))
//                .collect(Collectors.toList());
//
//        if (!conflictingShowtimes.isEmpty()) {
//            throw new AppException(ErrorCode.INVALID_KEY);
//        }
//
//        showtimeMapper.updateShowtime(showtime, request);
//        showtime.setEndTime(endTime);
//
//        showtime = showtimeRepository.save(showtime);
//        return showtimeMapper.toShowtimeResponse(showtime);
//    }
//
//    @Override
//    public void deleteShowtime(Integer id) {
//        if (!showtimeRepository.existsById(id)) {
//            throw new AppException(ErrorCode.SHOWTIME_NOT_EXISTED);
//        }
//        showtimeRepository.deleteById(id);
//    }
//
//
//    @Override
//    public List<SeatResponse> getAvailableSeats(Integer showtimeId) {
//        Showtime showtime = showtimeRepository.findById(showtimeId)
//                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));
//
//        // Get all seats in the room
//        List<Seat> allSeats = seatRepository.findByRoomIdOrderByName(showtime.getRoomId());
//
//        // Get booked seats for this showtime
//        List<Ticket> bookedTickets = ticketRepository.findByShowtimeIdAndStatus(showtimeId, true);
//        List<Integer> bookedSeatIds = bookedTickets.stream()
//                .map(Ticket::getSeatId)
//                .collect(Collectors.toList());
//
//        // Filter available seats
//        List<Seat> availableSeats = allSeats.stream()
//                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
//                .collect(Collectors.toList());
//
//        return availableSeats.stream()
//                .map(seatMapper::toSeatResponse)
//                .collect(Collectors.toList());
//    }
//
    @Override
    public List<ShowtimeResponse> getShowtimesByMovieId(Integer movieId) {
        LocalDateTime now = LocalDateTime.now();

        ApiResponse<MovieResponse> movie = movieServiceClient.getMovieById(movieId);
        if (movie.getResult() == null) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }


        return showtimeRepository.findByMovieId(movieId)
                .stream()
                .filter(showtime -> (showtime.getStartTime().isEqual(now) || showtime.getStartTime().isAfter(now) || showtime.getEndTime().isAfter(now)))
                .map(showtimeMapper::toShowtimeResponse)
                .collect(Collectors.toList());
    }
//
//    @Override
//    public List<ShowtimeResponse> getShowtimesByCinemaId(Integer cinemaId) {
//        LocalDateTime now = LocalDateTime.now();
//
//        if (!cinemaRepository.existsById(cinemaId)) {
//            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
//        }
//
//        return showtimeRepository.findByRoomCinemaId(cinemaId)
//                .stream()
//                .filter(showtime -> (showtime.getStartTime().isEqual(now) || showtime.getStartTime().isAfter(now)))
//                .map(showtimeMapper::toShowtimeResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ShowtimeResponse> getShowtimesByMovieAndCinema(Integer movieId, Integer cinemaId) {
//        LocalDateTime now = LocalDateTime.now();
//
//        if (!movieRepository.existsById(movieId)) {
//            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
//        }
//
//        if (!cinemaRepository.existsById(cinemaId)) {
//            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
//        }
//
//        return showtimeRepository.findByMovieIdAndRoomCinemaId(movieId, cinemaId)
//                .stream()
//                .filter(showtime -> (showtime.getStartTime().isEqual(now) || showtime.getStartTime().isAfter(now)))
//                .map(showtimeMapper::toShowtimeResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ShowtimeResponse> getShowtimesByMovieAndRoom(Integer movieId, Integer roomId) {
//        LocalDateTime now = LocalDateTime.now();
//
//        if (!movieRepository.existsById(movieId)) {
//            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
//        }
//
//        if (!roomRepository.existsById(roomId)) {
//            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
//        }
//
//        return showtimeRepository.findByMovieIdAndRoomId(movieId, roomId)
//                .stream()
//                .filter(showtime -> (showtime.getStartTime().isEqual(now) || showtime.getStartTime().isAfter(now)))
//                .map(showtimeMapper::toShowtimeResponse)
//                .collect(Collectors.toList());
//    }
//    public List<TicketResponse> getBookedTickets(Integer showtimeId) {
//        Showtime showtime = showtimeRepository.findById(showtimeId)
//                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));
//
//        // Get booked seats for this showtime
//        List<Ticket> bookedTickets = ticketRepository.findByShowtimeIdAndStatus(showtimeId, true);
//
//        return bookedTickets.stream()
//                .map(ticketMapper::toTicketResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<ShowtimeResponse> getShowtimesShowing() {
        LocalDateTime now = LocalDateTime.now();

        List<Showtime> showtimes = showtimeRepository.findAll()
                .stream()
                .filter(showtime -> showtime.getStartTime().isAfter(now))
                .collect(Collectors.toList());

        return showtimes.stream()
                .map(showtimeMapper::toShowtimeResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<ShowtimeResponse> getShowtimesByDateAndRoomId(LocalDate date, Integer roomId)
    {
        List<Showtime> showtimes = showtimeRepository.findShowtimesByDateAndRoom(date, roomId);
        return showtimes.stream()
                .map(showtimeMapper::toShowtimeResponse)
                .collect(Collectors.toList());
    };


}
