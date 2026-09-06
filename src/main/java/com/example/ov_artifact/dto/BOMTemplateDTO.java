package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMTemplateDTO {
    private String templateId;
    private String templateName;
    private String createdBy;
    private String creatorName;
    private String itemId;
    private String itemName;
    private List<BOMTemplateItemDTO> items;
}
