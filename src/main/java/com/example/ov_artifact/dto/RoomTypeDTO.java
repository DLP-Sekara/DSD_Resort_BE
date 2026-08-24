package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeDTO {
    private String typeId;
    private String typeName;
    private BigDecimal pricePerNight;
    private Integer maxOccupancy;
}
