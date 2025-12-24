package com.web.userservice.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpForgotPasswordRequest {
    private String email;
    private String otp;
}
