package com.web.recommendationservice.service.mail;

public interface MailService {
    void sendMail(String email, String subject, String content);
}
