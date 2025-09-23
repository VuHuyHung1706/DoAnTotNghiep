package com.web.showtimeservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeRequest {

    private Integer movieId;

    @NotNull(message = "ROOM_ID_NOT_NULL")
    private Integer roomId;

    @NotNull(message = "START_TIME_NOT_NULL")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer ticketPrice;
}
