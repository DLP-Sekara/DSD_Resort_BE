package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.DemandForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DemandForecastRepository extends JpaRepository<DemandForecast, String> {
    List<DemandForecast> findByTargetDate(LocalDate targetDate);
}
