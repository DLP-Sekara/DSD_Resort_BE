package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {
    private String guestId;
    private String name;
    private String phone;
    private String nic;
}
