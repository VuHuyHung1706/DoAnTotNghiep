package com.web.userservice.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
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
}
