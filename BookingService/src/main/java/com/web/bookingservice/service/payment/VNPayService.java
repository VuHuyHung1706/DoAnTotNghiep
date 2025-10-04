package com.web.bookingservice.service.payment;

import com.web.bookingservice.dto.request.PaymentRequest;
import com.web.bookingservice.dto.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest);
    boolean processPaymentCallback(HttpServletRequest request);
}
