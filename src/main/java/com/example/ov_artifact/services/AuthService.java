package com.example.ov_artifact.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ov_artifact.dto.AuthDTO;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.util.JwtUtil;

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

            // Password එක null කරන්න (Security purposes)
            dto.setPassword(null);
            return dto;
        }).collect(Collectors.toList());
    }

}
