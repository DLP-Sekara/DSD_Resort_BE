package com.example.ov_artifact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedBOMItemDTO {
    @JsonProperty("templateId")
    private String templateId;

    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("portionsCooked")
    private Integer portionsCooked;
}
