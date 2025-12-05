package com.web.cinemaservice.service.room;

import com.web.cinemaservice.dto.request.RoomRequest;
import com.web.cinemaservice.dto.response.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    Page<RoomResponse> getAllRooms(Pageable pageable);
    List<RoomResponse> getRoomsByCinemaId(Integer cinemaId);
    RoomResponse getRoomById(Integer id);
    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Integer id, RoomRequest request);
    void deleteRoom(Integer id);
    List<RoomResponse> searchRooms(String roomName, Integer cinemaId);
}
