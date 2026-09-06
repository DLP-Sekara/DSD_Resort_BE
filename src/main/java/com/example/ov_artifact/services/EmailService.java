package com.example.ov_artifact.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        log.info("========== FORGOT PASSWORD OTP ==========");
        log.info("Destination Email: {}", toEmail);
        log.info("Generated OTP Code: {}", otp);
        log.info("=========================================");

        if (mailSender != null && senderEmail != null && !senderEmail.trim().isEmpty()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(senderEmail);
                message.setTo(toEmail);
                message.setSubject("DSD Resort - Password Reset OTP Code");
                message.setText("Your OTP verification code for password reset is: " + otp + "\n\nThis code will expire in 5 minutes.");
                mailSender.send(message);
                log.info("OTP email sent successfully to {}", toEmail);
            } catch (Exception e) {
                log.warn("Could not send email via JavaMailSender (Check SMTP config). OTP logged to console above. Error: {}", e.getMessage());
            }
        } else {
            log.info("JavaMailSender or sender email is not fully configured in .env. OTP printed to console log for testing.");
        }
    }
}
