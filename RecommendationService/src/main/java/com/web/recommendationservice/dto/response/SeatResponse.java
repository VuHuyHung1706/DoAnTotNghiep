package com.web.recommendationservice.dto.response;

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
    private String seatName;
    private Integer roomId;
}
