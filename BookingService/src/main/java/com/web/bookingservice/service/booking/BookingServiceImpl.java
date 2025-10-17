package com.web.bookingservice.service.booking;

import com.web.bookingservice.constant.PaymentStatus;
import com.web.bookingservice.dto.request.BookingRequest;
import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.BookingResponse;
import com.web.bookingservice.dto.response.SeatResponse;
import com.web.bookingservice.dto.response.ShowtimeResponse;
import com.web.bookingservice.entity.Invoice;
import com.web.bookingservice.entity.Ticket;
import com.web.bookingservice.exception.AppException;
import com.web.bookingservice.exception.ErrorCode;
import com.web.bookingservice.mapper.TicketMapper;
import com.web.bookingservice.repository.InvoiceRepository;
import com.web.bookingservice.repository.TicketRepository;
import com.web.bookingservice.repository.client.CinemaServiceClient;
import com.web.bookingservice.repository.client.MovieServiceClient;
import com.web.bookingservice.service.qr.QRCodeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Autowired
    private MovieServiceClient movieServiceClient;

    @Override
    public BookingResponse bookTickets(BookingRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ApiResponse<ShowtimeResponse> showtimeResponseApiResponse = movieServiceClient.getShowtimeById(request.getShowtimeId());
        if (showtimeResponseApiResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(showtimeResponseApiResponse.getMessage()));
        }

        // Validate seats exist and are available
        List<SeatResponse> seats = new ArrayList<>();
        for (Integer seatId : request.getSeatIds()) {

            ApiResponse<SeatResponse> seatResponses = cinemaServiceClient.getSeatById(seatId);
            if (seatResponses.getCode() != 1000) {
                throw new AppException(ErrorCode.fromMessage(seatResponses.getMessage()));
            }

            // Check if seat is already booked for this showtime
            boolean isBooked = ticketRepository.existsByShowtimeIdAndSeatIdAndStatus(
                    request.getShowtimeId(), seatId, true);

            if (isBooked) {
                throw new AppException(ErrorCode.INVALID_KEY); // Seat already booked
            }

            seats.add(seatResponses.getResult());
        }

        // Calculate total amount
        int totalAmount = seats.size() * showtimeResponseApiResponse.getResult().getTicketPrice();

        // Create invoice
        Invoice invoice = Invoice.builder()
                .username(username)
                .totalAmount(totalAmount)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        invoice = invoiceRepository.save(invoice);

        // Create tickets
        List<Ticket> tickets = new ArrayList<>();

        for (SeatResponse seat : seats) {
            Ticket ticket = Ticket.builder()
                    .showtimeId(request.getShowtimeId())
                    .seatId(seat.getId())
                    .invoiceId(invoice.getId())
                    .price(showtimeResponseApiResponse.getResult().getTicketPrice())
                    .status(true) // Mark as booked
                    .createdAt(LocalDateTime.now())
                    .build();

            ticket = ticketRepository.save(ticket);

            // Generate QR code for the ticket
            String qrCode = qrCodeService.generateQRCode(ticket.getId());
            ticket.setQrCode(qrCode);
            ticket = ticketRepository.save(ticket);
            tickets.add(ticket);
        }

        // Build response
        return BookingResponse.builder()
                .invoiceId(invoice.getId())
                .username(username)
                .totalAmount(totalAmount)
                .paymentStatus(invoice.getPaymentStatus())
                .vnpayTransactionId(invoice.getVnpayTransactionId())
                .bookingTime(invoice.getCreatedAt())
                .paidAt(invoice.getPaidAt())
                .showtime(showtimeResponseApiResponse.getResult())
                .seats(seats)
                .tickets(tickets.stream().map(ticketMapper::toTicketResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public BookingResponse getBookingById(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTED));

        // Get tickets for this invoice
        List<Ticket> tickets = ticketRepository.findByInvoiceId(invoiceId);

        if (tickets.isEmpty()) {
            throw new AppException(ErrorCode.TICKET_NOT_EXISTED);
        }

        // Get showtime from first ticket (all tickets should have same showtime)
//        Showtime showtime = tickets.get(0).getShowtime();
        ApiResponse<ShowtimeResponse> showtimeResponse = movieServiceClient.getShowtimeById(tickets.get(0).getShowtimeId());

        // Get seats
//        List<Seat> seats = tickets.stream()
//                .map(Ticket::getSeat)
//                .collect(Collectors.toList());
        List<SeatResponse> seats = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ApiResponse<SeatResponse> seatResponses = cinemaServiceClient.getSeatById(ticket.getSeatId());
            if (seatResponses.getCode() != 1000) {
                throw new AppException(ErrorCode.fromMessage(seatResponses.getMessage()));
            }
            seats.add(seatResponses.getResult());
        }

        return BookingResponse.builder()
                .invoiceId(invoice.getId())
                .username(invoice.getUsername())
                .totalAmount(invoice.getTotalAmount())
                .paymentStatus(invoice.getPaymentStatus())
                .vnpayTransactionId(invoice.getVnpayTransactionId())
                .bookingTime(invoice.getCreatedAt() != null ? invoice.getCreatedAt() : LocalDateTime.now())
                .paidAt(invoice.getPaidAt())
                .showtime(showtimeResponse.getResult())
                .seats(seats)
                .tickets(tickets.stream().map(ticketMapper::toTicketResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<BookingResponse> getMyBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Get all invoices for the user
        List<Invoice> invoices = invoiceRepository.findByUsername(username);

        if (invoices.isEmpty()) {
            return new ArrayList<>(); // No bookings found
        }

        List<BookingResponse> bookings = new ArrayList<>();

        for (Invoice invoice : invoices) {
            // Get tickets for this invoice
            List<Ticket> tickets = ticketRepository.findByInvoiceId(invoice.getId());

            if (tickets.isEmpty()) {
                continue; // Skip if no tickets found
            }

            // Get showtime from first ticket (all tickets should have same showtime)
            ApiResponse<ShowtimeResponse> showtimeResponse = movieServiceClient.getShowtimeById(tickets.get(0).getShowtimeId());

            // Get seats
            List<SeatResponse> seats = new ArrayList<>();
            for (Ticket ticket : tickets) {
                ApiResponse<SeatResponse> seatResponses = cinemaServiceClient.getSeatById(ticket.getSeatId());
                if (seatResponses.getCode() != 1000) {
                    throw new AppException(ErrorCode.fromMessage(seatResponses.getMessage()));
                }
                seats.add(seatResponses.getResult());
            }
            BookingResponse booking = BookingResponse.builder()
                    .invoiceId(invoice.getId())
                    .username(invoice.getUsername())
                    .totalAmount(invoice.getTotalAmount())
                    .paymentStatus(invoice.getPaymentStatus())
                    .vnpayTransactionId(invoice.getVnpayTransactionId())
                    .bookingTime(invoice.getCreatedAt() != null ? invoice.getCreatedAt() : LocalDateTime.now())
                    .paidAt(invoice.getPaidAt())
                    .showtime(showtimeResponse.getResult())
                    .seats(seats)
                    .tickets(tickets.stream().map(ticketMapper::toTicketResponse).collect(Collectors.toList()))
                    .build();

            bookings.add(booking);
        }

        return bookings;
    }

    @Override
    public List<BookingResponse> getMyBookingsByUsername(String username) {
        // Get all invoices for the user
        List<Invoice> invoices = invoiceRepository.findByUsername(username);

        if (invoices.isEmpty()) {
            return new ArrayList<>(); // No bookings found
        }

        List<BookingResponse> bookings = new ArrayList<>();

        for (Invoice invoice : invoices) {
            // Get tickets for this invoice
            List<Ticket> tickets = ticketRepository.findByInvoiceId(invoice.getId());

            if (tickets.isEmpty()) {
                continue; // Skip if no tickets found
            }

            // Get showtime from first ticket (all tickets should have same showtime)
            ApiResponse<ShowtimeResponse> showtimeResponse = movieServiceClient.getShowtimeById(tickets.get(0).getShowtimeId());

            // Get seats
            List<SeatResponse> seats = new ArrayList<>();
            for (Ticket ticket : tickets) {
                ApiResponse<SeatResponse> seatResponses = cinemaServiceClient.getSeatById(ticket.getSeatId());
                if (seatResponses.getCode() != 1000) {
                    throw new AppException(ErrorCode.fromMessage(seatResponses.getMessage()));
                }
                seats.add(seatResponses.getResult());
            }
            BookingResponse booking = BookingResponse.builder()
                    .invoiceId(invoice.getId())
                    .username(invoice.getUsername())
                    .totalAmount(invoice.getTotalAmount())
                    .paymentStatus(invoice.getPaymentStatus())
                    .vnpayTransactionId(invoice.getVnpayTransactionId())
                    .bookingTime(invoice.getCreatedAt() != null ? invoice.getCreatedAt() : LocalDateTime.now())
                    .paidAt(invoice.getPaidAt())
                    .showtime(showtimeResponse.getResult())
                    .seats(seats)
                    .tickets(tickets.stream().map(ticketMapper::toTicketResponse).collect(Collectors.toList()))
                    .build();

            bookings.add(booking);
        }

        return bookings;
    }
}
