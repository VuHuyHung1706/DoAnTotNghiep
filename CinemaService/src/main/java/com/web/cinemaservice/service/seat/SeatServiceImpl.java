package com.web.cinemaservice.service.seat;

import com.web.cinemaservice.dto.request.SeatRequest;
import com.web.cinemaservice.dto.response.SeatResponse;
import com.web.cinemaservice.entity.Seat;
import com.web.cinemaservice.exception.AppException;
import com.web.cinemaservice.exception.ErrorCode;
import com.web.cinemaservice.mapper.SeatMapper;
import com.web.cinemaservice.repository.RoomRepository;
import com.web.cinemaservice.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeatMapper seatMapper;

    @Override
    public List<SeatResponse> getSeatsByRoomId(Integer roomId) {
        return seatRepository.findByRoomIdOrderByName(roomId)
                .stream()
                .map(seatMapper::toSeatResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SeatResponse getSeatById(Integer id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_EXISTED));
        return seatMapper.toSeatResponse(seat);
    }

    @Override
    public SeatResponse createSeat(SeatRequest request) {
        if (!roomRepository.existsById(request.getRoomId())) {
            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
        }

        Seat seat = seatMapper.toSeat(request);
        seat = seatRepository.save(seat);
        return seatMapper.toSeatResponse(seat);
    }

    @Override
    public SeatResponse updateSeat(Integer id, SeatRequest request) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_EXISTED));

        if (!roomRepository.existsById(request.getRoomId())) {
            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
        }

        seatMapper.updateSeat(seat, request);
        seat = seatRepository.save(seat);
        return seatMapper.toSeatResponse(seat);
    }

    @Override
    public void deleteSeat(Integer id) {
        if (!seatRepository.existsById(id)) {
            throw new AppException(ErrorCode.SEAT_NOT_EXISTED);
        }

        // For now, database constraints will handle this validation
        // If explicit check is needed, add BookingServiceClient similar to RoomService

        seatRepository.deleteById(id);
    }
}
