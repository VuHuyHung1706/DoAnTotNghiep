package com.web.recommendationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Integer invoiceId;
    private String username;
    private Integer totalAmount;
    private String paymentStatus;
    private String vnpayTransactionId;
    private LocalDateTime bookingTime;
    private LocalDateTime paidAt;
    private ShowtimeResponse showtime;
    private List<SeatResponse> seats;
    private List<TicketResponse> tickets;
}
