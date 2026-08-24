package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "DemandForecast")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "forecast_id", length = 36, nullable = false, updatable = false)
    private String forecastId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private BOMTemplate bomTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private SystemUsers createdBy;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "predicted_guests", nullable = false)
    private Integer predictedGuests;

    @Column(name = "weather_feature", length = 50)
    private String weatherFeature;

    @Column(name = "is_holiday")
    private Boolean isHoliday;
}
