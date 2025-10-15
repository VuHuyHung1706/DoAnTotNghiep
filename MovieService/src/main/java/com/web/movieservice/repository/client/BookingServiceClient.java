package com.web.movieservice.repository.client;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.SeatResponse;
import com.web.movieservice.dto.response.TicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-service", url = "${booking.service.url}")
public interface BookingServiceClient {

    @GetMapping("/tickets/showtime/{showtimeId}/booked")
    ApiResponse<List<TicketResponse>> getBookedTicketsByShowtimeId(@PathVariable Integer showtimeId);

    @GetMapping("/tickets/showtime/{showtimeId}/booked-seat-ids")
    ApiResponse<List<Integer>> getBookedSeatIdsByShowtimeId(@PathVariable Integer showtimeId);
}
