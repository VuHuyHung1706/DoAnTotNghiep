package com.web.cinemaservice.service.room;

import com.web.cinemaservice.dto.request.RoomRequest;
import com.web.cinemaservice.dto.response.ApiResponse;
import com.web.cinemaservice.dto.response.RoomResponse;
import com.web.cinemaservice.entity.Room;
import com.web.cinemaservice.exception.AppException;
import com.web.cinemaservice.exception.ErrorCode;
import com.web.cinemaservice.mapper.RoomMapper;
import com.web.cinemaservice.repository.CinemaRepository;
import com.web.cinemaservice.repository.RoomRepository;

import com.web.cinemaservice.repository.client.MovieServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private MovieServiceClient movieServiceClient;

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable)
                .map(roomMapper::toRoomResponse);
    }

    @Override
    public List<RoomResponse> getRoomsByCinemaId(Integer cinemaId) {
        return roomRepository.findByCinemaId(cinemaId)
                .stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        if (!cinemaRepository.existsById(request.getCinemaId())) {
            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
        }

        Room room = roomMapper.toRoom(request);
        room = roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public RoomResponse updateRoom(Integer id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        if (!cinemaRepository.existsById(request.getCinemaId())) {
            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
        }

        roomMapper.updateRoom(room, request);
        room = roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new AppException(ErrorCode.ROOM_NOT_EXISTED);
        }

        Room room = roomRepository.findById(id).get();
        if (!room.getSeats().isEmpty()) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ROOM_HAS_SEATS);
        }

        try {
            ApiResponse<Boolean> hasShowtimesResponse = movieServiceClient.hasShowtimesByRoomId(id);
            if (hasShowtimesResponse.getCode() == 1000 && hasShowtimesResponse.getResult()) {
                throw new AppException(ErrorCode.CANNOT_DELETE_ROOM_HAS_SHOWTIMES);
            }
        } catch (Exception e) {
            // If movie service is down or error, prevent deletion for safety
            throw new AppException(ErrorCode.CANNOT_DELETE_ROOM_HAS_SHOWTIMES);
        }

        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomResponse> searchRooms(String roomName, Integer cinemaId) {
        return roomRepository.searchRooms(roomName, cinemaId)
                .stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }
}
