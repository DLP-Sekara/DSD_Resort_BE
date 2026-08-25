package com.example.ov_artifact.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class OtpService {

    private final Map<String, OtpDetails> otpCache = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OtpDetails {
        private String otpCode;
        private LocalDateTime expiryTime;
        private boolean verified;
    }

    public String generateAndStoreOtp(String email) {
        int code = 100000 + random.nextInt(900000);
        String otpCode = String.valueOf(code);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        otpCache.put(email.toLowerCase(), new OtpDetails(otpCode, expiryTime, false));
        return otpCode;
    }

    public boolean verifyOtp(String email, String inputOtp) {
        OtpDetails details = otpCache.get(email.toLowerCase());
        if (details == null) {
            throw new IllegalArgumentException("No OTP requested for this email!");
        }

        if (LocalDateTime.now().isAfter(details.getExpiryTime())) {
            otpCache.remove(email.toLowerCase());
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        if (!details.getOtpCode().equals(inputOtp)) {
            throw new IllegalArgumentException("Invalid OTP code!");
        }

        details.setVerified(true);
        return true;
    }

    public boolean isOtpVerified(String email) {
        OtpDetails details = otpCache.get(email.toLowerCase());
        if (details == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(details.getExpiryTime())) {
            otpCache.remove(email.toLowerCase());
            return false;
        }
        return details.isVerified();
    }

    public void clearOtp(String email) {
        otpCache.remove(email.toLowerCase());
    }
}
