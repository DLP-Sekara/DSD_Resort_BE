package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialDTO {
    private String materialId;
    private String materialName;
    private String unitOfMeasure;
    private BigDecimal quantityOnHand;
    private String category;
}
