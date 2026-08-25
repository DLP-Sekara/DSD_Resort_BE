package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor 
@AllArgsConstructor
@Data 
@ToString 
public class AuthDTO {
    private String userId;
    private String name;
    private String password;
    private String email;
    private String role;
    private String token;
}
