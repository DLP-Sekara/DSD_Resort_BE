package com.example.ov_artifact.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandDateContextResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("DayOfWeek")
    private Integer dayOfWeek; // 0=Monday..6=Sunday

    private String dayName;

    @JsonProperty("IsWeekend")
    private Integer isWeekend; // 0 or 1

    @JsonProperty("IsHoliday")
    private Integer isHoliday; // 0 or 1

    private String holidayName;

    private String holidayType;

    @JsonProperty("Temperature")
    private Double temperature; // In Celsius

    @JsonProperty("Weather")
    private String weather; // "Clear", "Cloudy", or "Rainy"

    private String weatherDescription;

    private String location;
}
