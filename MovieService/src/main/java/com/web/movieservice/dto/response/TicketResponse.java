package com.web.movieservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String seatName;
    private Boolean isScanned;
    private CustomerResponse customer;
}
