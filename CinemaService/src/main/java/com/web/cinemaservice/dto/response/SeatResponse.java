package com.web.cinemaservice.dto.response;

import com.web.cinemaservice.constant.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {
    private Integer id;
    private String name;
    private Integer seatRow;
    private Integer seatColumn;
    private SeatType seatType;
    private Integer roomId;
}
