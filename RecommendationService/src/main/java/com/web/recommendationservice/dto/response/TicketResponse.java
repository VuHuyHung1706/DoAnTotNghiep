package com.web.recommendationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Integer id;
    private Integer showtimeId;
    private Integer seatId;
    private Integer invoiceId;
    private Integer price;
    private Boolean status;
    private String qrCode;
    private LocalDateTime createdAt;
    private LocalDateTime scannedAt;
    private Boolean isScanned;
    private String seatName;
}
