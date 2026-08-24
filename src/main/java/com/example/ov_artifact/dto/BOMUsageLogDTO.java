package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMUsageLogDTO {
    private String usageId;
    private String templateId;
    private String cookedBy;
    private LocalDateTime usageDate;
    private Integer portionsCooked;
}
