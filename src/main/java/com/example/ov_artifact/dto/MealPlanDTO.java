package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanDTO {
    private String planId;
    private String name;
    private BigDecimal price;
    private String planCode;
    private String status;
}
