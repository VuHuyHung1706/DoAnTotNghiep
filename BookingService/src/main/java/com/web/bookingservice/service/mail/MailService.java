package com.web.bookingservice.service.mail;

import com.web.bookingservice.entity.Ticket;

import java.util.List;

public interface MailService {
    void sendTicketEmail(String toEmail, List<Ticket> tickets, String username) throws Exception;
}
