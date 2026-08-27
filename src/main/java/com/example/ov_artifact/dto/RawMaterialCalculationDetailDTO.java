package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCalculationDetailDTO {
    private String materialId;
    private String materialName;
    private String category;
    private String unitOfMeasure;
    private BigDecimal qtyPerPerson;
    private Integer orderedQty;
    private BigDecimal totalRequiredQty;
    private BigDecimal quantityOnHand;
    private String status; // "In Stock" or "Shortage"
    private Boolean isShortage;
    private BigDecimal shortageQty;
}
