package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodItemDTO {
    private String itemId;
    private String name;
    private BigDecimal unitPrice;
    private Integer quantityOnHand;
    private Boolean isKitchenPrepared;
}
