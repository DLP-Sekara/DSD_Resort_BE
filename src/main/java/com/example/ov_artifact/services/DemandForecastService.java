package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.DemandForecastDTO;
import com.example.ov_artifact.entity.BOMTemplate;
import com.example.ov_artifact.entity.DemandForecast;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.BOMTemplateRepository;
import com.example.ov_artifact.repository.DemandForecastRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandForecastService {

    private final DemandForecastRepository forecastRepository;
    private final BOMTemplateRepository bomTemplateRepository;
    private final AuthRepo authRepo;
    private final ModelMapper modelMapper;

    public DemandForecastDTO addForecast(DemandForecastDTO dto) {
        BOMTemplate template = bomTemplateRepository.findById(dto.getTemplateId())
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found with ID: " + dto.getTemplateId()));

        SystemUsers createdBy = authRepo.findById(dto.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + dto.getCreatedBy()));

        DemandForecast forecast = new DemandForecast();
        forecast.setBomTemplate(template);
        forecast.setCreatedBy(createdBy);
        forecast.setTargetDate(dto.getTargetDate());
        forecast.setPredictedGuests(dto.getPredictedGuests());
        forecast.setWeatherFeature(dto.getWeatherFeature());
        forecast.setIsHoliday(dto.getIsHoliday());

        DemandForecast savedForecast = forecastRepository.save(forecast);
        return modelMapper.map(savedForecast, DemandForecastDTO.class);
    }

    public DemandForecastDTO getForecastById(String forecastId) {
        DemandForecast forecast = forecastRepository.findById(forecastId)
                .orElseThrow(() -> new RuntimeException("DemandForecast not found with ID: " + forecastId));
        return modelMapper.map(forecast, DemandForecastDTO.class);
    }

    public List<DemandForecastDTO> getForecastsByTargetDate(LocalDate targetDate) {
        List<DemandForecast> forecasts = forecastRepository.findByTargetDate(targetDate);
        return modelMapper.map(forecasts, new TypeToken<List<DemandForecastDTO>>() {}.getType());
    }

    public List<DemandForecastDTO> getAllForecasts() {
        List<DemandForecast> forecasts = forecastRepository.findAll();
        return modelMapper.map(forecasts, new TypeToken<List<DemandForecastDTO>>() {}.getType());
    }

    public void deleteForecast(String forecastId) {
        if (!forecastRepository.existsById(forecastId)) {
            throw new RuntimeException("DemandForecast not found with ID: " + forecastId);
        }
        forecastRepository.deleteById(forecastId);
    }
}
