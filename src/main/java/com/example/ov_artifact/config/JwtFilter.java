package com.example.ov_artifact.config;

import com.example.ov_artifact.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        String email = null;

        // 1. Check Authorization Bearer Header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. Check "jwt" cookie if header not present
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Extract Email if there is a Token
        if (token != null) {
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {
                email = null;
            }
        }

        // 4. Input User & Authorities to SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, email)) {
                String role = jwtUtil.extractRole(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (role != null && !role.trim().isEmpty()) {
                    String upperRole = role.trim().toUpperCase();
                    // Spring Security hasRole('XYZ') checks for 'ROLE_XYZ'
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + upperRole));

                    // Normalize CHEF and HEAD_CHEF
                    if ("HEAD_CHEF".equals(upperRole)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_CHEF"));
                    } else if ("CHEF".equals(upperRole)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_HEAD_CHEF"));
                    }
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}