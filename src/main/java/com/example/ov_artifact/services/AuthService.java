package com.example.ov_artifact.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ov_artifact.dto.AuthDTO;
import com.example.ov_artifact.dto.ChangePasswordRequestDTO;
import com.example.ov_artifact.dto.ResetPasswordRequestDTO;
import com.example.ov_artifact.dto.SendOtpRequestDTO;
import com.example.ov_artifact.dto.VerifyOtpRequestDTO;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepo authRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;

    public void registerUser(AuthDTO authDTO) {
        if (authRepo.findByName(authDTO.getName()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        authDTO.setPassword(passwordEncoder.encode(authDTO.getPassword()));
        SystemUsers user = modelMapper.map(authDTO, SystemUsers.class);
        authRepo.save(user);
    }

    public AuthDTO loginUser(AuthDTO authDTO) {
        SystemUsers user = authRepo.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new AccessDeniedException("User not found!"));

        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new AccessDeniedException("Invalid Password!");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        AuthDTO responseDTO = modelMapper.map(user, AuthDTO.class);
        responseDTO.setPassword(null);
        responseDTO.setToken(token);
        return responseDTO;
    }

    public AuthDTO checkSession(String email) {
        SystemUsers user = authRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Session invalid or User not found"));

        AuthDTO authDTO = modelMapper.map(user, AuthDTO.class);
        authDTO.setPassword(null);
        return authDTO;
    }

    public List<AuthDTO> getAllSystemUsers() {
        List<SystemUsers> users = authRepo.findAll();
        return users.stream().map(user -> {
            AuthDTO dto = new AuthDTO();

            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());

            dto.setPassword(null);
            return dto;
        }).collect(Collectors.toList());
    }

    public void sendForgotPasswordOtp(SendOtpRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required!");
        }

        SystemUsers user = authRepo.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with registered email: " + requestDTO.getEmail()));

        String otp = otpService.generateAndStoreOtp(user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void verifyForgotPasswordOtp(VerifyOtpRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getOtp() == null) {
            throw new IllegalArgumentException("Email and OTP are required!");
        }

        if (!authRepo.existsByEmail(requestDTO.getEmail())) {
            throw new EntityNotFoundException("User not found with registered email: " + requestDTO.getEmail());
        }

        otpService.verifyOtp(requestDTO.getEmail(), requestDTO.getOtp());
    }

    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getNewPassword() == null || requestDTO.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Email, new password, and confirm password are required!");
        }

        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match!");
        }

        if (requestDTO.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long!");
        }

        if (!otpService.isOtpVerified(requestDTO.getEmail())) {
            throw new IllegalArgumentException("OTP has not been verified for this email. Please verify OTP first.");
        }

        SystemUsers user = authRepo.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with registered email: " + requestDTO.getEmail()));

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        authRepo.save(user);

        otpService.clearOtp(requestDTO.getEmail());
    }

    public void changePassword(String email, ChangePasswordRequestDTO requestDTO) {
        if (requestDTO.getCurrentPassword() == null || requestDTO.getNewPassword() == null || requestDTO.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Current password, new password, and confirm password are required!");
        }

        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match!");
        }

        if (requestDTO.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long!");
        }

        SystemUsers user = authRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect!");
        }

        if (passwordEncoder.matches(requestDTO.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password!");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        authRepo.save(user);
    }

}
