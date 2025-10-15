package com.web.cinemaservice.service.seat;

import com.web.cinemaservice.dto.request.SeatRequest;
import com.web.cinemaservice.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {
    List<SeatResponse> getSeatsByRoomId(Integer roomId);
    SeatResponse getSeatById(Integer id);
    SeatResponse createSeat(SeatRequest request);
    SeatResponse updateSeat(Integer id, SeatRequest request);
    void deleteSeat(Integer id);
}
