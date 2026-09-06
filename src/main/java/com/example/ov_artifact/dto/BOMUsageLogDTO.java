package com.example.ov_artifact.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMUsageLogDTO {
    private String usageId;
    private String templateId;
    private String templateName;
    private String itemId;
    private String itemName;
    private String cookedBy;
    private String cookerName;
    private LocalDateTime usageDate;
    private Integer portionsCooked;

    @JsonProperty("Used_BOMs")
    @JsonAlias({"usedBOMs", "used_boms", "usedBoms"})
    private List<UsedBOMItemDTO> usedBOMs;
}
