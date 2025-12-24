package com.web.userservice.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendMail(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set("otp:" + email, otp, 5, TimeUnit.MINUTES);

        // Send OTP email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Verification Code");
        message.setText("Your OTP code is: " + otp + ". This code will expire in 5 minutes.");
        mailSender.send(message);
    }

    public Boolean verifyOtp(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get("otp:" + email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            return false;
        }

        // Delete Opt Redis
        redisTemplate.delete("otp:" + email);

        return true;
    }

    @Override
    public void sendOtpForgotPassword(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set("forgot_password_otp:" + email, otp, 5, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mã OTP Đặt Lại Mật Khẩu");
        message.setText("Mã OTP của bạn là: " + otp + "\n\nMã này sẽ hết hạn sau 5 phút.\n\nNếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.");
        mailSender.send(message);
    }

    @Override
    public Boolean verifyOtpForgotPassword(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get("forgot_password_otp:" + email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            return false;
        }

        // Mark OTP as verified, keep it for password reset confirmation
        redisTemplate.opsForValue().set("forgot_password_verified:" + email, "true", 15, TimeUnit.MINUTES);
        redisTemplate.delete("forgot_password_otp:" + email);

        return true;
    }

    @Override
    public void sendResetPasswordLink(String email, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Đặt Lại Mật Khẩu");
        message.setText("Xin chào,\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấp vào liên kết bên dưới để đặt lại mật khẩu của bạn:\n\n" +
                resetLink + "\n\n" +
                "Liên kết này sẽ hết hạn sau 15 phút.\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\nĐội ngũ hỗ trợ");
        mailSender.send(message);
    }
}
