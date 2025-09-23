package com.web.userservice.service.mail;

public interface MailService {
    void sendMail(String email);
    Boolean verifyOtp(String email, String otp);
}
