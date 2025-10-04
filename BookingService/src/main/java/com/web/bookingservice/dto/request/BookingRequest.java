package com.web.bookingservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull(message = "SHOWTIME_ID_NOT_NULL")
    private Integer showtimeId;

    @NotEmpty(message = "SEAT_SELECTION_REQUIRED")
    private List<Integer> seatIds;
}
