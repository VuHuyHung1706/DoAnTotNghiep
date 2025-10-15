package com.web.bookingservice.service.ticket;

import com.web.bookingservice.dto.request.ScanTicketRequest;
import com.web.bookingservice.dto.response.*;
import com.web.bookingservice.entity.Ticket;
import com.web.bookingservice.exception.AppException;
import com.web.bookingservice.exception.ErrorCode;
import com.web.bookingservice.mapper.TicketMapper;
import com.web.bookingservice.repository.InvoiceRepository;
import com.web.bookingservice.repository.TicketRepository;
import com.web.bookingservice.repository.client.CinemaServiceClient;
import com.web.bookingservice.repository.client.MovieServiceClient;
import com.web.bookingservice.repository.client.UserServiceClient;
import com.web.bookingservice.service.qr.QRCodeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MovieServiceClient  movieServiceClient;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public List<TicketDetailResponse> getMyTickets() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Get all invoices for the user and then get tickets
        return invoiceRepository.findByUsername(username)
                .stream()
                .flatMap(invoice -> ticketRepository.findByInvoiceId(invoice.getId()).stream())
                .map(ticketMapper::toTicketDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDetailResponse getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_EXISTED));

        // Check if the ticket belongs to the current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!ticket.getInvoice().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return ticketMapper.toTicketDetailResponse(ticket);
    }

    @Override
    public byte[] generateTicketQRCode(Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_EXISTED));

        // Check if the ticket belongs to the current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!ticket.getInvoice().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Generate QR code if not exists
        if (ticket.getQrCode() == null) {
            String qrCode = qrCodeService.generateQRCode(ticketId);
            ticket.setQrCode(qrCode);
            ticketRepository.save(ticket);
        }

        return qrCodeService.generateQRCodeImage(ticket.getQrCode());
    }

    @Override
    public ScanTicketResponse scanTicket(ScanTicketRequest request) {
        // Validate QR code format
        if (!qrCodeService.validateQRCode(request.getQrCode())) {
            return ScanTicketResponse.builder()
                    .success(false)
                    .message("Invalid QR code format")
                    .build();
        }

        // Extract ticket ID from QR code
        String[] parts = request.getQrCode().split("\\|");
        Integer ticketId = Integer.parseInt(parts[0]);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(null);

        if (ticket == null) {
            return ScanTicketResponse.builder()
                    .success(false)
                    .message("Ticket not found")
                    .build();
        }

        if (!ticket.getStatus()) {
            return ScanTicketResponse.builder()
                    .success(false)
                    .message("Ticket is not active")
                    .build();
        }

        if (ticket.getIsScanned()) {
            return ScanTicketResponse.builder()
                    .success(false)
                    .message("Ticket already scanned at " + ticket.getScannedAt())
                    .ticket(ticketMapper.toTicketDetailResponse(ticket))
                    .build();
        }

        // Check if showtime has started (optional validation)
        LocalDateTime now = LocalDateTime.now();

        ApiResponse<ShowtimeResponse> showtimeResponse = movieServiceClient.getShowtimeById(ticket.getShowtimeId());


//        if (now.isBefore(showtimeResponse.getResult().getStartTime().minusMinutes(60))) {
//
//            throw new AppException(ErrorCode.TICKET_NOT_READY);
////            return ScanTicketResponse.builder()
////                    .success(false)
////                    .message("Too early to scan ticket. Showtime starts at " + showtimeResponse.getResult().getStartTime())
////                    .ticket(ticketMapper.toTicketDetailResponse(ticket))
////                    .build();
//        }

        // Mark ticket as scanned
        ticket.setIsScanned(true);
        ticket.setScannedAt(now);
        ticketRepository.save(ticket);

        return ScanTicketResponse.builder()
                .success(true)
                .message("Ticket scanned successfully")
                .ticket(ticketMapper.toTicketDetailResponse(ticket))
                .scannedAt(now)
                .build();
    }

    @Override
    public List<TicketDetailResponse> getTicketsByShowtime(Integer showtimeId) {
        return ticketRepository.findByShowtimeIdAndStatus(showtimeId, true)
                .stream()
                .map(ticketMapper::toTicketDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketResponse> getBookedTicketsByShowtimeId(Integer showtimeId) {
        return ticketRepository.findByShowtimeIdAndStatus(showtimeId, true)
                .stream()
                .map(ticket -> {
                    TicketResponse response = ticketMapper.toTicketResponse(ticket);
                    ApiResponse<SeatResponse> seatResponseApiResponse = cinemaServiceClient.getSeatById(response.getSeatId());
                    response.setSeatName(seatResponseApiResponse.getResult().getName());
                    ApiResponse<CustomerResponse> customerResponseApiResponse = userServiceClient.getCustomerByUsername(ticket.getInvoice().getUsername());
                    CustomerResponse customerResponse = customerResponseApiResponse.getResult();
                    response.setCustomer(customerResponseApiResponse.getResult());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getBookedSeatIdsByShowtimeId(Integer showtimeId) {
        return ticketRepository.findByShowtimeIdAndStatus(showtimeId, true)
                .stream()
                .map(Ticket::getSeatId)
                .collect(Collectors.toList());
    }
}
