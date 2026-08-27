package com.example.ov_artifact;

import com.example.ov_artifact.dto.AuthDTO;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.services.AuthService;
import com.example.ov_artifact.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepo authRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private AuthDTO authDTO;
    private SystemUsers user;

    @BeforeEach
    void setUp() {
        authDTO = new AuthDTO();
        authDTO.setName("Saman Kumara");
        authDTO.setEmail("saman@gmail.com");
        authDTO.setPassword("1234");
        authDTO.setRole("ADMIN");

        user = new SystemUsers();
        user.setUserId("uuid-1234");
        user.setName("Saman Kumara");
        user.setEmail("saman@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("ADMIN");
    }

    @Test
    void testRegisterUser_Success() {

        when(authRepo.findByName(authDTO.getName())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authDTO.getPassword())).thenReturn("encodedPassword");
        when(modelMapper.map(any(AuthDTO.class), eq(SystemUsers.class))).thenReturn(user);

        assertDoesNotThrow(() -> authService.registerUser(authDTO));

        verify(authRepo, times(1)).save(any(SystemUsers.class));
    }

    @Test
    void testRegisterUser_UsernameTaken() {

        when(authRepo.findByName(authDTO.getName())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.registerUser(authDTO));
    }

    @Test
    void testLoginUser_Success() {
        when(authRepo.findByEmail(authDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole())).thenReturn("mocked-jwt-token");
        when(modelMapper.map(any(SystemUsers.class), eq(AuthDTO.class))).thenReturn(authDTO);

        AuthDTO result = authService.loginUser(authDTO);

        assertNotNull(result);
        assertEquals("mocked-jwt-token", result.getToken());
        assertNull(result.getPassword());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        when(authRepo.findByEmail(authDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> authService.loginUser(authDTO));
    }

    @Test
    void testCheckSession_Success() {
        when(authRepo.findByEmail("saman@gmail.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(any(SystemUsers.class), eq(AuthDTO.class))).thenReturn(authDTO);

        AuthDTO result = authService.checkSession("saman@gmail.com");

        assertNotNull(result);
        assertEquals("saman@gmail.com", result.getEmail());
    }
}