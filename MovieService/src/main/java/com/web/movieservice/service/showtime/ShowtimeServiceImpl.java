package com.web.movieservice.service.showtime;

import com.web.movieservice.dto.request.ShowtimeRequest;
import com.web.movieservice.dto.response.*;
import com.web.movieservice.entity.Movie;
import com.web.movieservice.entity.Showtime;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.mapper.ShowtimeMapper;
import com.web.movieservice.repository.MovieRepository;
import com.web.movieservice.repository.ShowtimeRepository;
import com.web.movieservice.repository.client.BookingServiceClient;
import com.web.movieservice.repository.client.CinemaServiceClient;
import com.web.movieservice.repository.client.RecommendationServiceClient;
import com.web.movieservice.service.recommendation.SendRecommendationMail;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShowtimeServiceImpl implements ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RecommendationServiceClient recommendationServiceClient;

    @Autowired
    private SendRecommendationMail sendRecommendtionMail;

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
//
    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @Autowired
    private ShowtimeMapper showtimeMapper;

//    @Autowired
//    private SeatMapper seatMapper;
//
//    @Autowired
//    private TicketMapper ticketMapper;

    @Override
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShowtimeResponse> getAllShowtimes(Pageable pageable) {
        return showtimeRepository.findAll(pageable)
                .map(this::mapToShowtimeResponseWithDetails);
    }

    @Override
    public ShowtimeResponse getShowtimeById(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));
        ShowtimeResponse showtimeResponse = showtimeMapper.toShowtimeResponse(showtime);

        ApiResponse<RoomResponse> roomResponseApiResponse = cinemaServiceClient.getRoomById(showtimeResponse.getRoomId());

        if (roomResponseApiResponse.getCode() != 1000)
        {
            throw new AppException(ErrorCode.fromMessage(roomResponseApiResponse.getMessage()));
        }

        RoomResponse roomResponse = roomResponseApiResponse.getResult();

        showtimeResponse.setCinemaName(roomResponse.getCinemaName());
        showtimeResponse.setRoomName(roomResponse.getName());
        showtimeResponse.setRoom(roomResponse);

        return showtimeResponse;
    }

    @Override
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

//        Room room = roomRepository.findById(request.getRoomId())
//                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

            // Calculate end time based on movie duration
        LocalDateTime endTimeOnMovie = request.getStartTime().plusMinutes(movie.getDuration());

        if (request.getEndTime() == null) {
            request.setEndTime(endTimeOnMovie);
        }
        else if (request.getEndTime().isBefore(endTimeOnMovie)) {
            throw new AppException(ErrorCode.SHOWTIME_INVALID_TIME);
        }

        // Check for conflicting showtimes in the same room
        List<Showtime> conflictingShowtimes = showtimeRepository.findConflictingShowtimes(
                request.getRoomId(), request.getStartTime(), request.getEndTime());

        if (!conflictingShowtimes.isEmpty()) {
            throw new AppException(ErrorCode.SHOWTIME_CONFLICTING); // You might want to create a specific error for this
        }

        Showtime showtime = showtimeMapper.toShowtime(request);
        showtime.setEndTime(request.getEndTime());
        showtime.setMovie(movie);
        showtime = showtimeRepository.save(showtime);

        sendRecommendtionMail.startAsyncTask(showtime.getMovieId(), showtime);

        return showtimeMapper.toShowtimeResponse(showtime);
    }

    @Override
    public ShowtimeResponse updateShowtime(Integer id, ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));

        // Validate movie exists
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        // Validate room exists
//        if (!roomRepository.existsById(request.getRoomId())) {
//            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
//        }

        // Calculate end time based on movie duration
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        // Check for conflicting showtimes in the same room (excluding current showtime)
        List<Showtime> conflictingShowtimes = showtimeRepository.findConflictingShowtimes(
                request.getRoomId(), request.getStartTime(), endTime);

        conflictingShowtimes = conflictingShowtimes.stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());

        if (!conflictingShowtimes.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        showtimeMapper.updateShowtime(showtime, request);
        showtime.setEndTime(endTime);

        showtime = showtimeRepository.save(showtime);
        return showtimeMapper.toShowtimeResponse(showtime);
    }

    @Override
    public void deleteShowtime(Integer id) {
        if (!showtimeRepository.existsById(id)) {
            throw new AppException(ErrorCode.SHOWTIME_NOT_EXISTED);
        }
        showtimeRepository.deleteById(id);
    }


    @Override
    public List<SeatResponse> getAvailableSeats(Integer showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));

        // Get all seats
//        List<Seat> allSeats = seatRepository.findByRoomIdOrderByName(showtime.getRoomId());

        ApiResponse<RoomResponse> roomResponses = cinemaServiceClient.getRoomById(showtime.getRoomId());
        if (roomResponses.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(roomResponses.getMessage()));
        }

        List<SeatResponse> allSeats = roomResponses.getResult().getSeats();

        // Get booked seats
//        List<Ticket> bookedTickets = ticketRepository.findByShowtimeIdAndStatus(showtimeId, true);
//        List<Integer> bookedSeatIds = bookedTickets.stream()
//                .map(Ticket::getSeatId)
//                .collect(Collectors.toList());

        ApiResponse<List<Integer>> bookedSeatIdsResponse = bookingServiceClient.getBookedSeatIdsByShowtimeId(showtimeId);
        if (bookedSeatIdsResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(bookedSeatIdsResponse.getMessage()));
        }

        List<Integer> bookedSeatIds = bookedSeatIdsResponse.getResult();

        // Filter available seats
        return allSeats.stream()
                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByMovieId(Integer movieId) {
        LocalDateTime now = LocalDateTime.now();

        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        return showtimeRepository.findByMovieId(movieId)
                .stream()
                .filter(showtime -> showtime.getEndTime().isAfter(now))
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByCinemaId(Integer cinemaId) {
        LocalDateTime now = LocalDateTime.now();

        ApiResponse<CinemaResponse> cinemaApiResponse = cinemaServiceClient.getCinemaById(cinemaId);
        if (cinemaApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(cinemaApiResponse.getMessage()));
        }

        CinemaResponse cinemaResponse = cinemaApiResponse.getResult();
        List<Integer> roomIds = cinemaResponse.getRooms().stream().map(RoomResponse::getId).toList();


        return showtimeRepository.findByRoomIdIn(roomIds)
                .stream()
                .filter(showtime -> (showtime.getEndTime().isAfter(now)))
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByMovieAndCinema(Integer movieId, Integer cinemaId) {
        LocalDateTime now = LocalDateTime.now();

        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

        ApiResponse<CinemaResponse> cinemaApiResponse = cinemaServiceClient.getCinemaById(cinemaId);
        if (cinemaApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(cinemaApiResponse.getMessage()));
        }

        CinemaResponse cinemaResponse = cinemaApiResponse.getResult();
        List<Integer> roomIds = cinemaResponse.getRooms().stream().map(RoomResponse::getId).toList();

        return showtimeRepository.findByMovieIdAndRoomIdIn(movieId, roomIds)
                .stream()
                .filter(showtime -> (showtime.getEndTime().isAfter(now)))
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByMovieAndRoom(Integer movieId, Integer roomId) {
        LocalDateTime now = LocalDateTime.now();

        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_EXISTED);
        }

//        if (!roomRepository.existsById(roomId)) {
//            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
//        }        if (!roomRepository.existsById(roomId)) {
//            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
//        }

        return showtimeRepository.findByMovieIdAndRoomId(movieId, roomId)
                .stream()
                .filter(showtime -> (showtime.getEndTime().isAfter(now)))
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getBookedTickets(Integer showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_EXISTED));

        ApiResponse<List<TicketResponse>> listApiResponse = bookingServiceClient.getBookedTicketsByShowtimeId(showtimeId);

        if (listApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(listApiResponse.getMessage()));
        }

        return listApiResponse.getResult();
    }

    @Override
    public List<ShowtimeResponse> getShowtimesShowing() {
        LocalDateTime now = LocalDateTime.now();

        List<Showtime> showtimes = showtimeRepository.findAll()
                .stream()
                .filter(showtime -> showtime.getStartTime().isAfter(now))
                .collect(Collectors.toList());

        return showtimes.stream()
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    }


    @Override
    public List<ShowtimeResponse> getShowtimesByDateAndRoomId(LocalDate date, Integer roomId)
    {
        List<Showtime> showtimes = showtimeRepository.findShowtimesByDateAndRoom(date, roomId);
        return showtimes.stream()
                .map(this::mapToShowtimeResponseWithDetails)
                .collect(Collectors.toList());
    };

    private ShowtimeResponse mapToShowtimeResponseWithDetails(Showtime showtime) {
        ShowtimeResponse showtimeResponse = showtimeMapper.toShowtimeResponse(showtime);

        ApiResponse<RoomResponse> roomResponseApiResponse = cinemaServiceClient.getRoomById(showtimeResponse.getRoomId());
        if (roomResponseApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(roomResponseApiResponse.getMessage()));
        }
        RoomResponse roomResponse = roomResponseApiResponse.getResult();
        showtimeResponse.setCinemaName(roomResponse.getCinemaName());
        showtimeResponse.setRoomName(roomResponse.getName());
        showtimeResponse.setRoom(roomResponse);

        return showtimeResponse;
    }

}
