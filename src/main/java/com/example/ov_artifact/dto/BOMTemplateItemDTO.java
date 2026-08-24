package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMTemplateItemDTO {
    private String templateItemId;
    private String templateId;
    private String materialId;
    private BigDecimal qtyPerPerson;
}
