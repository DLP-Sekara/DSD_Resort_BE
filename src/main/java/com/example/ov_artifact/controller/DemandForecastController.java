package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.DemandForecastDTO;
import com.example.ov_artifact.services.DemandForecastService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/demand-forecasts")
@RequiredArgsConstructor
public class DemandForecastController {

    private final DemandForecastService forecastService;

    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addForecast(@RequestBody DemandForecastDTO dto) {
        DemandForecastDTO savedForecast = forecastService.addForecast(dto);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Demand Forecast Added Successfully", savedForecast),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getForecastById(@PathVariable String id) {
        DemandForecastDTO forecast = forecastService.getForecastById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Demand Forecast Fetched Successfully", forecast),
                HttpStatus.OK);
    }

    @GetMapping("/by-date")
    public ResponseEntity<StandardResponse> getForecastsByTargetDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DemandForecastDTO> forecasts = forecastService.getForecastsByTargetDate(date);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Demand Forecasts Fetched Successfully", forecasts),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllForecasts() {
        List<DemandForecastDTO> forecasts = forecastService.getAllForecasts();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Demand Forecasts Fetched Successfully", forecasts),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteForecast(@PathVariable String id) {
        forecastService.deleteForecast(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Demand Forecast Deleted Successfully", null),
                HttpStatus.OK);
    }
}
