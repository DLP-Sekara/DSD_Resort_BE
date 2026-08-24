package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailDTO {
    private String detailId;
    private String resId;
    private String itemId;
    private Integer orderedQty;
}
