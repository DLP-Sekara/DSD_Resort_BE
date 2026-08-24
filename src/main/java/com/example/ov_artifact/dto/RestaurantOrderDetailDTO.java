package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantOrderDetailDTO {
    private String orderId;
    private String itemId;
    private Integer orderedQty;
}
