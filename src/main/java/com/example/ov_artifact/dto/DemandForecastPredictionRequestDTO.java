package com.example.ov_artifact.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandForecastPredictionRequestDTO {

    @JsonProperty("DayOfWeek")
    @JsonAlias({"dayOfWeek", "day_of_week"})
    private Integer dayOfWeek;

    @JsonProperty("IsWeekend")
    @JsonAlias({"isWeekend", "is_weekend"})
    private Integer isWeekend;

    @JsonProperty("IsHoliday")
    @JsonAlias({"isHoliday", "is_holiday"})
    private Integer isHoliday;

    @JsonProperty("Temperature")
    @JsonAlias({"temperature", "temp"})
    private Double temperature;

    @JsonProperty("Weather")
    @JsonAlias({"weather"})
    private String weather;

    @JsonProperty("targetDate")
    @JsonAlias({"target_date", "date"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate targetDate;
}
