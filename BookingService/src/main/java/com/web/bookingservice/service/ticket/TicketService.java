package com.web.bookingservice.service.ticket;

import com.web.bookingservice.dto.request.ScanTicketRequest;
import com.web.bookingservice.dto.response.ScanTicketResponse;
import com.web.bookingservice.dto.response.TicketDetailResponse;
import com.web.bookingservice.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {
    List<TicketDetailResponse> getMyTickets();
    TicketDetailResponse getTicketById(Integer id);
    byte[] generateTicketQRCode(Integer ticketId);
    ScanTicketResponse scanTicket(ScanTicketRequest request);
    List<TicketDetailResponse> getTicketsByShowtime(Integer showtimeId);
    List<TicketResponse> getBookedTicketsByShowtimeId(Integer showtimeId);
    List<Integer> getBookedSeatIdsByShowtimeId(Integer showtimeId);

    boolean hasTicketsByShowtimeId(Integer showtimeId);
    boolean hasTicketsBySeatId(Integer seatId);
}
