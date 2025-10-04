package com.web.bookingservice.repository.client;

import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.SeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cinema-service", url = "${cinema.service.url}")
public interface CinemaServiceClient {
    @GetMapping("/{id}")
    ApiResponse<SeatResponse> getSeatById(@PathVariable Integer id);
}
