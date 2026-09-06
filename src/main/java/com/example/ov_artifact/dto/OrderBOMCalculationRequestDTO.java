package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBOMCalculationRequestDTO {
    private String orderId;
    private List<OrderItemDetailDTO> orderDetails;
}
