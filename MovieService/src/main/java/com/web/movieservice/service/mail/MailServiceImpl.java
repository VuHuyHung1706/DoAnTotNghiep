package com.web.movieservice.service.mail;

import com.web.movieservice.dto.response.RoomResponse;
import com.web.movieservice.entity.Movie;
import com.web.movieservice.entity.Showtime;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String email, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendMovieRecommendationEmail(String toEmail, Movie movie, Showtime showtime, RoomResponse room)
            throws MessagingException, IOException {

        // 1️⃣ Đọc file template HTML
        ClassPathResource resource = new ClassPathResource("templates/movie_recommendation_template.html");
        String template = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        // 2️⃣ Format dữ liệu
        String startTime = showtime.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
        String ticketPrice = NumberFormat.getInstance(new Locale("vi", "VN"))
                .format(showtime.getTicketPrice()) + " VND";
        String bookingUrl = "http://localhost:3000";

        // 3️⃣ Thay placeholder trong HTML
        String htmlContent = template
                .replace("${movieTitle}", movie.getTitle())
                .replace("${poster}", movie.getPoster())
                .replace("${description}", movie.getDescription())
                .replace("${duration}", String.valueOf(movie.getDuration()))
                .replace("${language}", movie.getLanguage())
                .replace("${cinemaName}", room.getCinemaName())
                .replace("${roomName}", room.getName())
                .replace("${startTime}", startTime)
                .replace("${ticketPrice}", ticketPrice)
                .replace("${trailer}", movie.getTrailer())
                .replace("${bookingUrl}", bookingUrl);

        // 4️⃣ Gửi mail HTML
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
        );
        helper.setTo(toEmail);
        helper.setSubject("🎬[HHK Cinema] Gợi ý phim hay: " + movie.getTitle());
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
