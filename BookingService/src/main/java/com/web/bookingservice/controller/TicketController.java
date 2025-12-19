package com.web.bookingservice.controller;

import com.web.bookingservice.dto.request.ScanTicketRequest;
import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.ScanTicketResponse;
import com.web.bookingservice.dto.response.TicketDetailResponse;
import com.web.bookingservice.dto.response.TicketResponse;
import com.web.bookingservice.service.ticket.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/showtime/{showtimeId}/exists")
    public ApiResponse<Boolean> hasTicketsByShowtimeId(@PathVariable Integer showtimeId) {
        boolean exists = ticketService.hasTicketsByShowtimeId(showtimeId);
        return ApiResponse.<Boolean>builder()
                .code(1000)
                .result(exists)
                .build();
    }

    @GetMapping("/seat/{seatId}/exists")
    public ApiResponse<Boolean> hasTicketsBySeatId(@PathVariable Integer seatId) {
        boolean exists = ticketService.hasTicketsBySeatId(seatId);
        return ApiResponse.<Boolean>builder()
                .code(1000)
                .result(exists)
                .build();
    }

    @GetMapping("/my-tickets")
    public ApiResponse<List<TicketDetailResponse>> getMyTickets() {
        return ApiResponse.<List<TicketDetailResponse>>builder()
                .result(ticketService.getMyTickets())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<TicketDetailResponse> getTicketById(@PathVariable Integer id) {
        return ApiResponse.<TicketDetailResponse>builder()
                .result(ticketService.getTicketById(id))
                .build();
    }

    @GetMapping("/{id}/qr-code")
    public ResponseEntity<byte[]> generateTicketQRCode(@PathVariable Integer id) {
        byte[] qrCodeImage = ticketService.generateTicketQRCode(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);
        headers.set("Content-Disposition", "inline; filename=ticket-" + id + "-qr.png");

        return ResponseEntity.ok()
                .headers(headers)
                .body(qrCodeImage);
    }

    @PostMapping("/scan")
    public ApiResponse<ScanTicketResponse> scanTicket(@Valid @RequestBody ScanTicketRequest request) {
        return ApiResponse.<ScanTicketResponse>builder()
                .result(ticketService.scanTicket(request))
                .build();
    }

    @GetMapping("/showtime/{showtimeId}")
    public ApiResponse<List<TicketDetailResponse>> getTicketsByShowtime(@PathVariable Integer showtimeId) {
        return ApiResponse.<List<TicketDetailResponse>>builder()
                .result(ticketService.getTicketsByShowtime(showtimeId))
                .build();
    }

    @GetMapping("/showtime/{showtimeId}/booked")
    public ApiResponse<List<TicketResponse>> getBookedTicketsByShowtimeId(@PathVariable Integer showtimeId) {
        return ApiResponse.<List<TicketResponse>>builder()
                .result(ticketService.getBookedTicketsByShowtimeId(showtimeId))
                .build();
    }

    @GetMapping("/showtime/{showtimeId}/booked-seat-ids")
    public ApiResponse<List<Integer>> getBookedSeatIdsByShowtimeId(@PathVariable Integer showtimeId) {
        return ApiResponse.<List<Integer>>builder()
                .result(ticketService.getBookedSeatIdsByShowtimeId(showtimeId))
                .build();
    }
}
