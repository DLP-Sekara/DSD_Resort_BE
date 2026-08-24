package com.example.ov_artifact.controller;

import com.example.ov_artifact.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<StandardResponse> checkHealth() {
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Server is Up and Running!", null),
                HttpStatus.OK
        );
    }
}