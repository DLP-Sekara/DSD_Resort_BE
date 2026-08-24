package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandForecastDTO {
    private String forecastId;
    private String templateId;
    private String createdBy;
    private LocalDate targetDate;
    private Integer predictedGuests;
    private String weatherFeature;
    private Boolean isHoliday;
}
