package com.example.ov_artifact.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ov_artifact.dto.AuthDTO;
import com.example.ov_artifact.services.AuthService;
import com.example.ov_artifact.util.StandardResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<StandardResponse> signUp(@RequestBody AuthDTO authDTO) {
        authService.registerUser(authDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "User Saved Successfully", null),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> login(@RequestBody AuthDTO authDTO, HttpServletResponse response) {
        AuthDTO loggedUser = authService.loginUser(authDTO);
        String token = loggedUser.getToken();
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(cookie);

        loggedUser.setToken(null);

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Login Successful!", loggedUser),
                HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<StandardResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Logout Successful!", null),
                HttpStatus.OK);
    }

    @GetMapping("/check-session")
    public ResponseEntity<StandardResponse> checkSession() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        AuthDTO userDetails = authService.checkSession(email);

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Session is valid", userDetails),
                HttpStatus.OK);
    }

    @GetMapping("/all-users")
    public ResponseEntity<StandardResponse> getAllSystemUsers() {

        List<AuthDTO> allUsers = authService.getAllSystemUsers();

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "All System Users Fetched Successfully", allUsers),
                HttpStatus.OK);
    }

}
