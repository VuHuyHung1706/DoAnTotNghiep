package com.web.recommendationservice.repository.client;

import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-service", url = "${booking.service.url}")
public interface BookingServiceClient {

    @GetMapping("/bookings/my-bookings")
    ApiResponse<List<BookingResponse>> getMyBookings();

    @GetMapping("/bookings/user/paid-booking/{username}")
    ApiResponse<List<BookingResponse>> getPaidBookingByUsername(@PathVariable String username);

}
