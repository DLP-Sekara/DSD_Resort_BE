package com.example.ov_artifact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBOMCalculationResultDTO {
    @JsonProperty("ItemId")
    private String itemId;

    private String itemName;

    @JsonProperty("required_quantity")
    private Integer requiredQuantity;

    private List<RawMaterialCalculationDetailDTO> rawMaterialDetails = new ArrayList<>();

    // Additional getters for camelCase compatibility if accessed without jackson alias
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(Integer requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }
}

