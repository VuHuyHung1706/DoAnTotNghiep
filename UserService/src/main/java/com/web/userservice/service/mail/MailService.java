package com.web.userservice.service.mail;

public interface MailService {
    void sendMail(String email);
    Boolean verifyOtp(String email, String otp);
    void sendOtpForgotPassword(String email);
    Boolean verifyOtpForgotPassword(String email, String otp);
    void sendResetPasswordLink(String email, String resetToken);
}
