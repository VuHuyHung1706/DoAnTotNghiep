package com.web.bookingservice.controller;

import com.web.bookingservice.dto.request.PaymentRequest;
import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.PaymentResponse;
import com.web.bookingservice.service.payment.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @PostMapping("/vnpay/create")
    public ApiResponse<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<PaymentResponse>builder()
                .result(vnPayService.createPayment(request, httpRequest))
                .build();
    }

    @GetMapping("/vnpay/callback")
    public void paymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = vnPayService.processPaymentCallback(request);
        String invoiceId = request.getParameter("vnp_TxnRef");

        if (success) {
            response.sendRedirect(frontendUrl + "/payment/success?invoiceId=" + invoiceId + "&status=success");
        } else {
            response.sendRedirect(frontendUrl + "/payment/success?invoiceId=" + invoiceId + "&status=failed");
        }
    }
}
