package com.web.movieservice.service.mail;

import com.web.movieservice.dto.response.RoomResponse;
import com.web.movieservice.entity.Movie;
import com.web.movieservice.entity.Showtime;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface MailService {
    void sendMail(String email, String subject, String content);
    void sendMovieRecommendationEmail(String toEmail, Movie movie, Showtime showtime, RoomResponse room)
            throws MessagingException, IOException;
}