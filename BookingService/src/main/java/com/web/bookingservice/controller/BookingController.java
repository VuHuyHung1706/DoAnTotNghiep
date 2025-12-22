package com.web.bookingservice.controller;

import com.web.bookingservice.dto.request.BookingRequest;
import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.BookingResponse;
import com.web.bookingservice.service.booking.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ApiResponse<BookingResponse> bookTickets(@Valid @RequestBody BookingRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.bookTickets(request))
                .build();
    }

    @GetMapping("/{invoiceId}")
    public ApiResponse<BookingResponse> getBookingById(@PathVariable Integer invoiceId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingById(invoiceId))
                .build();
    }

    @GetMapping("/my-bookings")
    public ApiResponse<List<BookingResponse>> getMyBookings() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyBookings())
                .build();
    }

    @GetMapping("/user/{username}")
    public ApiResponse<List<BookingResponse>> getBookingByUsername(@PathVariable String username) {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyBookingsByUsername(username))
                .build();
    }

    @GetMapping("/user/paid-booking/{username}")
    public ApiResponse<List<BookingResponse>> getPaidBookingByUsername(@PathVariable String username) {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyPaidBookingsByUsername(username))
                .build();
    }
}
