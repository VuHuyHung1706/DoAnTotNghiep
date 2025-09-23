package com.web.movieservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private Integer id;
    private String name;
    private Integer totalSeats;
    private Integer cinemaId;
    private String cinemaName;
    private List<SeatResponse> seats;
}
