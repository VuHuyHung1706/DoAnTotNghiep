package com.web.bookingservice.service.booking;


import com.web.bookingservice.dto.request.BookingRequest;
import com.web.bookingservice.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse bookTickets(BookingRequest request);
    BookingResponse getBookingById(Integer invoiceId);
    List<BookingResponse> getMyBookings();
    List<BookingResponse> getMyBookingsByUsername(String username);
    List<BookingResponse> getMyPaidBookingsByUsername(String username);
    }
