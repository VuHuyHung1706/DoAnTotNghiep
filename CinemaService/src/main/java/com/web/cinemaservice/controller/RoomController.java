package com.web.cinemaservice.controller;

import com.web.cinemaservice.dto.request.RoomRequest;
import com.web.cinemaservice.dto.response.ApiResponse;
import com.web.cinemaservice.dto.response.RoomResponse;
import com.web.cinemaservice.service.room.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/all")
    public ApiResponse<List<RoomResponse>> getAllRooms() {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getAllRooms())
                .build();
    }

    @GetMapping
    public ApiResponse<Page<RoomResponse>> getAllRooms(Pageable pageable) {
        return ApiResponse.<Page<RoomResponse>>builder()
                .result(roomService.getAllRooms(pageable))
                .build();
    }

    @GetMapping("/cinema/{cinemaId}")
    public ApiResponse<List<RoomResponse>> getRoomsByCinemaId(@PathVariable Integer cinemaId) {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getRoomsByCinemaId(cinemaId))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable Integer id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getRoomById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.createRoom(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ApiResponse.<String>builder()
                .result("Room deleted successfully")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<RoomResponse>> searchRooms(
            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) Integer cinemaId) {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.searchRooms(roomName, cinemaId))
                .build();
    }

}
