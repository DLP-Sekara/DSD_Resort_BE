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

    @Column(name = "template_id", columnDefinition = "TEXT", nullable = false)
    private String templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, columnDefinition = "varchar(36)")
    private SystemUsers createdBy;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "date_details")
    private String dateDetails;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "predicted_guests", nullable = false)
    private Integer predictedGuests;

    @Column(name = "weather_feature", length = 50)
    private String weatherFeature;

    @Column(name = "is_holiday")
    private Boolean isHoliday;
}
