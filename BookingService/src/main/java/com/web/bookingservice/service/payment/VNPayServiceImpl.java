package com.web.bookingservice.service.payment;

import com.web.bookingservice.config.VNPayConfig;
import com.web.bookingservice.constant.PaymentStatus;
import com.web.bookingservice.dto.request.PaymentRequest;
import com.web.bookingservice.dto.response.PaymentResponse;
import com.web.bookingservice.dto.response.ShowtimeResponse;
import com.web.bookingservice.entity.Invoice;
import com.web.bookingservice.entity.Ticket;
import com.web.bookingservice.exception.AppException;
import com.web.bookingservice.exception.ErrorCode;
import com.web.bookingservice.repository.InvoiceRepository;
import com.web.bookingservice.repository.TicketRepository;
import com.web.bookingservice.repository.client.MovieServiceClient;
import com.web.bookingservice.repository.client.RecommendationServiceClient;
import com.web.bookingservice.repository.client.UserServiceClient;
import com.web.bookingservice.service.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VNPayServiceImpl extends VNPayConfig implements VNPayService {

    @Autowired
    private RecommendationServiceClient recommendationServiceClient;

    @Autowired
    private MovieServiceClient movieServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MailService mailService;

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.ipn-url}")
    private String vnp_IpnUrl;

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.secret-key}")
    private String secretKey;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {
        // Get invoice
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTED));

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = invoice.getTotalAmount() * 100L; // VNPay requires amount in VND * 100
        String vnp_TxnRef = getRandomNumber(8);
        String vnp_IpAddr = getIpAddress(httpRequest);

        String vnp_TmnCode = this.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan ve xem phim");
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
//        vnp_Params.put("vnp_IpnUrl", vnp_IpnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        // Update invoice with transaction reference
        invoice.setVnpayTransactionId(vnp_TxnRef);
        invoiceRepository.save(invoice);

        return PaymentResponse.builder()
                .paymentUrl(paymentUrl)
                .invoiceId(request.getInvoiceId().toString())
                .amount(String.valueOf(amount))
                .txnRef(vnp_TxnRef)
                .build();
    }

    @Override
    public boolean processPaymentCallback(HttpServletRequest request) {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = hashAllFields(fields);

        if (signValue.equals(vnp_SecureHash)) {
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

            // Find invoice by transaction reference
            Invoice invoice = invoiceRepository.findByVnpayTransactionId(vnp_TxnRef);
            if (invoice != null) {
                if ("00".equals(vnp_ResponseCode)) {
                    // Payment successful
                    invoice.setPaymentStatus(PaymentStatus.PAID);
                    invoice.setPaidAt(LocalDateTime.now());
                    invoiceRepository.save(invoice);

                    // Send email with ticket QR codes
                    try {
                        List<Ticket> tickets = ticketRepository.findByInvoiceId(invoice.getId());
                        String recipientEmail = getRecipientEmail(invoice);

                        if (recipientEmail != null && !recipientEmail.isEmpty()) {
                            mailService.sendTicketEmail(recipientEmail, tickets, invoice.getUsername());
                            log.info("Ticket email sent successfully to: {}", recipientEmail);
                        } else {
                            log.warn("No email found for invoice {}, skipping email notification", invoice.getId());
                        }
                    } catch (Exception e) {
                        log.error("[ERROR] Failed to send ticket email for invoice {}: {}",
                                invoice.getId(), e.getMessage(), e);
                        // Don't fail the payment process if email fails
                    }

                } else {
                    // Payment failed
                    ticketRepository.deleteAll(ticketRepository.findByInvoiceId(invoice.getId()));
                    invoiceRepository.delete(invoice);
                }
                return "00".equals(vnp_ResponseCode);
            }

        }
        return false;
    }

    private String getRecipientEmail(Invoice invoice) {
        // If invoice has customer email (guest user), use it
        if (invoice.getCustomerEmail() != null && !invoice.getCustomerEmail().isEmpty()) {
            return invoice.getCustomerEmail();
        }

        // Otherwise, get email from UserService for registered users
        try {
            var customerResponse = userServiceClient.getCustomerByUsername(invoice.getUsername());
            if (customerResponse != null && customerResponse.getResult() != null) {
                return customerResponse.getResult().getEmail();
            }
        } catch (Exception e) {
            log.error("[ERROR] Failed to get customer email for username {}: {}",
                    invoice.getUsername(), e.getMessage());
        }

        return null;
    }

    private void createDefaultReviewsForInvoice(Invoice invoice) {
        // Get all tickets for this invoice
        List<Ticket> tickets = ticketRepository.findByInvoiceId(invoice.getId());

        // Get unique movie IDs from the tickets
        Set<Integer> movieIds = tickets.stream()
                .map(ticket -> {
                    try {
                        ShowtimeResponse showtime = movieServiceClient.getShowtimeById(ticket.getShowtimeId()).getResult();
                        return showtime.getMovieId();
                    } catch (Exception e) {
                        log.error("[ERROR] Failed to get showtime {} for ticket {}: {}",
                                ticket.getShowtimeId(), ticket.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Create default review for each unique movie
        for (Integer movieId : movieIds) {
            try {
                movieServiceClient.createDefaultReview(invoice.getUsername(), movieId);
                log.info("Created default review for user {} and movie {}", invoice.getUsername(), movieId);
            } catch (Exception e) {
                log.error("[ERROR] Failed to create default review for user {} and movie {}: {}",
                        invoice.getUsername(), movieId, e.getMessage());
            }
        }
    }
}
