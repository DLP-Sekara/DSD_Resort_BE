package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantOrderDTO {
    private String orderId;
    private String guestId;
    private String handledBy;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private String status;
    private List<RestaurantOrderDetailDTO> orderDetails;
}
