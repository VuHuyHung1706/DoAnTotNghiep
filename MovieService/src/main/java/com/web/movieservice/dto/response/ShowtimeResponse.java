package com.web.movieservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeResponse {
    private Integer id;
    private Integer movieId;
    private Integer roomId;
    private String roomName;
    private String cinemaName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer ticketPrice;
}
