package com.web.bookingservice.service.mail;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.RoomResponse;
import com.web.bookingservice.dto.response.SeatResponse;
import com.web.bookingservice.dto.response.ShowtimeResponse;
import com.web.bookingservice.entity.Ticket;
import com.web.bookingservice.repository.client.CinemaServiceClient;
import com.web.bookingservice.repository.client.MovieServiceClient;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MovieServiceClient movieServiceClient;

    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Override
    public void sendTicketEmail(String toEmail, List<Ticket> tickets, String username) throws Exception {
        if (tickets.isEmpty()) {
            throw new IllegalArgumentException("No tickets to send");
        }

        // Get showtime info from first ticket
        Ticket firstTicket = tickets.get(0);
        ApiResponse<ShowtimeResponse> showtimeResponse = movieServiceClient.getShowtimeById(firstTicket.getShowtimeId());
        ShowtimeResponse showtime = showtimeResponse.getResult();

        // Get seat info for all tickets
        StringBuilder seatNames = new StringBuilder();
        for (int i = 0; i < tickets.size(); i++) {
            ApiResponse<SeatResponse> seatResponse = cinemaServiceClient.getSeatById(tickets.get(i).getSeatId());
            seatNames.append(seatResponse.getResult().getName());
            if (i < tickets.size() - 1) {
                seatNames.append(", ");
            }
        }

        // Get room info
        ApiResponse<RoomResponse> roomResponse = cinemaServiceClient.getRoomById(showtime.getRoomId());
        RoomResponse room = roomResponse.getResult();

        // Read HTML template
        ClassPathResource resource = new ClassPathResource("templates/ticket_email_template.html");
        String template = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        // Format data
        String startTime = showtime.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String startDate = showtime.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String totalPrice = NumberFormat.getInstance(new Locale("vi", "VN"))
                .format(tickets.stream().mapToInt(Ticket::getPrice).sum()) + " VNĐ";

        // Generate QR codes and embed them
        StringBuilder ticketRows = new StringBuilder();
        Map<String, ByteArrayResource> qrCodeImages = new HashMap<>();

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            ApiResponse<SeatResponse> seatResponse = cinemaServiceClient.getSeatById(ticket.getSeatId());
            String seatName = seatResponse.getResult().getName();

            // Generate QR code image
            byte[] qrCodeBytes = generateQRCodeImage(ticket.getQrCode(), 300, 300);
            String qrCodeCid = "qrcode" + ticket.getId();
            qrCodeImages.put(qrCodeCid, new ByteArrayResource(qrCodeBytes));

            ticketRows.append("<tr>")
                    .append("<td style='padding:15px;border-bottom:1px solid #eee;text-align:center;font-weight:600;'>")
                    .append(seatName)
                    .append("</td>")
                    .append("<td style='padding:15px;border-bottom:1px solid #eee;text-align:center;'>")
                    .append("<img src='cid:").append(qrCodeCid).append("' alt='QR Code' style='width:150px;height:150px;'/>")
                    .append("</td>")
                    .append("</tr>");
        }

        // Replace placeholders
        String htmlContent = template
                .replace("${username}", username)
                .replace("${movieTitle}", showtime.getMovie().getTitle())
                .replace("${cinemaName}", room.getCinemaName())
                .replace("${roomName}", room.getName())
                .replace("${startTime}", startTime)
                .replace("${startDate}", startDate)
                .replace("${seatNames}", seatNames.toString())
                .replace("${totalPrice}", totalPrice)
                .replace("${ticketCount}", String.valueOf(tickets.size()))
                .replace("${ticketRows}", ticketRows.toString());

        // Send email with embedded QR codes
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(toEmail);
        helper.setSubject("🎬 [HHK Cinema] Vé xem phim của bạn - " + showtime.getMovie().getTitle());
        helper.setText(htmlContent, true);

        // Attach QR code images
        for (Map.Entry<String, ByteArrayResource> entry : qrCodeImages.entrySet()) {
            helper.addInline(entry.getKey(), entry.getValue(), "image/png");
        }

        mailSender.send(message);
        log.info("Ticket email sent successfully to: {}", toEmail);
    }

    private byte[] generateQRCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
