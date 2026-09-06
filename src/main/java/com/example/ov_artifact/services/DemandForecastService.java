package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.DemandForecastDTO;
import com.example.ov_artifact.entity.DemandForecast;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.DemandForecastRepository;

import com.example.ov_artifact.dto.DemandDateContextResponseDTO;
import com.example.ov_artifact.dto.DemandForecastPredictionRequestDTO;
import com.example.ov_artifact.dto.DemandForecastPredictionResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandForecastService {

    private final DemandForecastRepository forecastRepository;
    private final AuthRepo authRepo;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;

    @Value("${forecast.service.url}")
    private String forecastServiceUrl;

    @Value("${openweather.api.key:}")
    private String openWeatherApiKey;

    @Value("${openweather.api.city:Colombo,LK}")
    private String defaultWeatherCity;

    @Value("${calendarific.api.key}")
    private String calendarificApiKey;

    private final Map<Integer, List<Map<String, Object>>> holidayCache = new ConcurrentHashMap<>();

    public DemandDateContextResponseDTO getDateContext(LocalDate targetDate, String requestedCity) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        String city = (requestedCity != null && !requestedCity.trim().isEmpty()) ? requestedCity.trim() : defaultWeatherCity;

        int dayOfWeek = date.getDayOfWeek().getValue() - 1; // 0=Monday..6=Sunday
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int isWeekend = (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) ? 1 : 0;

        HolidayResult holidayResult = checkSriLankaHoliday(date);
        WeatherResult weatherResult = fetchWeather(date, city);
        return DemandDateContextResponseDTO.builder()
                .date(date)
                .dayOfWeek(dayOfWeek)
                .dayName(dayName)
                .isWeekend(isWeekend)
                .isHoliday(holidayResult.isHoliday ? 1 : 0)
                .holidayName(holidayResult.holidayName)
                .holidayType(holidayResult.holidayType)
                .temperature(weatherResult.temperature)
                .weather(weatherResult.weather)
                .weatherDescription(weatherResult.description)
                .location(city)
                .build();
    }

    private HolidayResult checkSriLankaHoliday(LocalDate date) {
        int year = date.getYear();
        
        try {
        List<Map<String, Object>> holidays = holidayCache.computeIfAbsent(year, y -> {
            try {
                String apiKey = calendarificApiKey;
                String url = "https://calendarific.com/api/v2/holidays?api_key=" + apiKey + "&country=LK&year=" + y;
                
                ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
                
                if (responseEntity.getBody() != null) {
                    Map<String, Object> body = responseEntity.getBody();
                    Map<String, Object> meta = (Map<String, Object>) body.get("meta");
                    
                
                    if (meta != null && Integer.valueOf(200).equals(meta.get("code"))) {
                        Map<String, Object> responseData = (Map<String, Object>) body.get("response");
                        
                        if (responseData != null && responseData.containsKey("holidays")) {
                            List<Map<String, Object>> apiHolidays = (List<Map<String, Object>>) responseData.get("holidays");
                            List<Map<String, Object>> formattedHolidays = new ArrayList<>();
                            
                            for (Map<String, Object> item : apiHolidays) {
                                Map<String, Object> dateObj = (Map<String, Object>) item.get("date");
                                if (dateObj != null && dateObj.containsKey("iso")) {
                                    String isoDate = (String) dateObj.get("iso");
                                    String justDate = isoDate.split("T")[0]; 
                                    
                                    Map<String, Object> holiday = new HashMap<>();
                                    holiday.put("date", justDate);
                                    holiday.put("name", item.get("name"));
                                    
                                    String type = (String) item.get("primary_type");
                                    if (type == null && item.get("type") instanceof List) {
                                        List<String> types = (List<String>) item.get("type");
                                        if (!types.isEmpty()) {
                                            type = types.get(0);
                                        }
                                    }
                                    holiday.put("type", type);
                                    
                                    formattedHolidays.add(holiday);
                                }
                            }
                            return formattedHolidays;
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Calendarific API Error: " + ex.getMessage());
            }
            return Collections.emptyList();
        });

        String targetDateStr = date.toString();
        for (Map<String, Object> h : holidays) {
            if (targetDateStr.equals(h.get("date"))) {
                return new HolidayResult(true, (String) h.get("name"), (String) h.get("type"));
            }
        }
    } catch (Exception e) {
        System.err.println("Cache Processing Error: " + e.getMessage());
    }
        return new HolidayResult(false, null, null);
    }

    @SuppressWarnings("unchecked")
    private WeatherResult fetchWeather(LocalDate date, String city) {
        
        // --- TEMPORARILY COMMENTED OUT OPENWEATHER API ---
        /*
        if (openWeatherApiKey != null && !openWeatherApiKey.trim().isEmpty()) {
            try {
                String encodedCity = city.replace(" ", "%20");
                String owmUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&appid=" + openWeatherApiKey.trim() + "&units=metric";
                Map<String, Object> root = restTemplate.getForObject(owmUrl, Map.class);
                
                if (root != null && root.containsKey("list")) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) root.get("list");
                    
                    if (list != null && !list.isEmpty()) {
                        String targetDatePrefix = date.toString();
                        double totalTemp = 0.0;
                        int count = 0;
                        Map<String, Integer> weatherFrequency = new HashMap<>();
                        String dominantWeatherMain = "Clear";
                        String dominantWeatherDesc = "clear sky";

                        for (Map<String, Object> item : list) {
                            String dtTxt = (String) item.get("dt_txt");
                            if (dtTxt != null && dtTxt.startsWith(targetDatePrefix)) {
                                Map<String, Object> mainMap = (Map<String, Object>) item.get("main");
                                if (mainMap != null && mainMap.get("temp") != null) {
                                    totalTemp += ((Number) mainMap.get("temp")).doubleValue();
                                    count++;
                                }
        
                                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) item.get("weather");
                                if (weatherList != null && !weatherList.isEmpty()) {
                                    Map<String, Object> firstWeather = weatherList.get(0);
                                    String main = (String) firstWeather.getOrDefault("main", "Clear");
                                    String desc = (String) firstWeather.getOrDefault("description", "clear sky");
                                    weatherFrequency.put(main + "|" + desc, weatherFrequency.getOrDefault(main + "|" + desc, 0) + 1);
                                }
                            }
                        }

                        if (count > 0) {
                            double avgTemp = Math.round((totalTemp / count) * 10.0) / 10.0;
        
                            int maxFreq = -1;
                            for (Map.Entry<String, Integer> entry : weatherFrequency.entrySet()) {
                                if (entry.getValue() > maxFreq) {
                                    maxFreq = entry.getValue();
                                    String[] parts = entry.getKey().split("\\|");
                                    dominantWeatherMain = parts[0];
                                    dominantWeatherDesc = parts[1];
                                }
                            }
                            return new WeatherResult(avgTemp, classifyWeather(dominantWeatherMain, null), dominantWeatherDesc);
                        }
                    }
                }
            } catch (Exception ex) {
                // Fall through to Open-Meteo fallback
            }
        }
        */

        // Default fallback result
        WeatherResult result = new WeatherResult(28.0, "Clear", "Pleasant Tropical Weather");

        // --- NEW DYNAMIC OPEN-METEO API ---
        try {
            String targetDateStr = date.toString();
            String omUrl = "https://api.open-meteo.com/v1/forecast?latitude=6.0367&longitude=80.2170&daily=weathercode,temperature_2m_max,temperature_2m_min&timezone=Asia/Colombo&start_date=" + targetDateStr + "&end_date=" + targetDateStr;
            Map<String, Object> root = restTemplate.getForObject(omUrl, Map.class);
            
            if (root != null && root.containsKey("daily")) {
                Map<String, Object> daily = (Map<String, Object>) root.get("daily");
                List<Number> maxTempList = (List<Number>) daily.get("temperature_2m_max");
                List<Number> minTempList = (List<Number>) daily.get("temperature_2m_min");
                List<Number> codeList = (List<Number>) daily.get("weathercode");

                if (maxTempList != null && !maxTempList.isEmpty() && 
                    minTempList != null && !minTempList.isEmpty() && 
                    codeList != null && !codeList.isEmpty()) {
                    
                    double max = maxTempList.get(0) != null ? maxTempList.get(0).doubleValue() : 28.0;
                    double min = minTempList.get(0) != null ? minTempList.get(0).doubleValue() : 28.0;
                    double avgTemp = Math.round(((max + min) / 2.0) * 10.0) / 10.0;
                    int weatherCode = codeList.get(0) != null ? codeList.get(0).intValue() : 0;
                    
                    result = new WeatherResult(avgTemp, classifyWeather(null, weatherCode), describeWeatherCode(weatherCode));
                }
            }
        } catch (Exception ex) {
            System.err.println("Open-Meteo API Error: " + ex.getMessage());
        }

        return result;
    }


    private String classifyWeather(String mainCondition, Integer weatherCode) {
        if (mainCondition != null && !mainCondition.isBlank()) {
            String lower = mainCondition.toLowerCase();
            if (lower.contains("rain") || lower.contains("drizzle") || lower.contains("thunder") || lower.contains("storm") || lower.contains("shower")) {
                return "Rainy";
            }
            if (lower.contains("cloud") || lower.contains("overcast") || lower.contains("fog") || lower.contains("mist") || lower.contains("haze")) {
                return "Cloudy";
            }
            if (lower.contains("clear") || lower.contains("sun")) {
                return "Clear";
            }
        }
        if (weatherCode != null) {
            if (weatherCode == 0 || weatherCode == 1) return "Clear";
            if (weatherCode == 2 || weatherCode == 3 || (weatherCode >= 45 && weatherCode <= 48)) return "Cloudy";
            if (weatherCode >= 51 && weatherCode <= 99) return "Rainy";
        }
        return "Clear";
    }

    private String describeWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Mainly clear, partly cloudy and overcast";
            case 45, 48 -> "Fog and depositing rime fog";
            case 51, 53, 55 -> "Drizzle: light, moderate, and dense intensity";
            case 61, 63, 65 -> "Rain: slight, moderate and heavy intensity";
            case 80, 81, 82 -> "Rain showers: slight, moderate, and violent";
            case 95, 96, 99 -> "Thunderstorm: slight or moderate with hail";
            default -> "Tropical Weather";
        };
    }

    private static class HolidayResult {
        final boolean isHoliday;
        final String holidayName;
        final String holidayType;

        HolidayResult(boolean isHoliday, String holidayName, String holidayType) {
            this.isHoliday = isHoliday;
            this.holidayName = holidayName;
            this.holidayType = holidayType;
        }
    }

    private static class WeatherResult {
        final double temperature;
        final String weather;
        final String description;

        WeatherResult(double temperature, String weather, String description) {
            this.temperature = temperature;
            this.weather = weather;
            this.description = description;
        }
    }

    public DemandForecastPredictionResponseDTO predictDemand(DemandForecastPredictionRequestDTO dto) {
        if (dto == null) {
            dto = new DemandForecastPredictionRequestDTO();
        }

        LocalDate targetDate = dto.getTargetDate();
        if (targetDate != null) {
            if (dto.getDayOfWeek() == null) {
                // Java DayOfWeek: Monday=1..Sunday=7. Python ML model expects: 0=Monday..6=Sunday
                dto.setDayOfWeek(targetDate.getDayOfWeek().getValue() - 1);
            }
            if (dto.getIsWeekend() == null) {
                boolean isWeekend = targetDate.getDayOfWeek() == DayOfWeek.SATURDAY || targetDate.getDayOfWeek() == DayOfWeek.SUNDAY;
                dto.setIsWeekend(isWeekend ? 1 : 0);
            }
        }

        int dayOfWeek = dto.getDayOfWeek() != null ? dto.getDayOfWeek() : 0;
        int isWeekend = dto.getIsWeekend() != null ? dto.getIsWeekend() : 0;
        int isHoliday = dto.getIsHoliday() != null ? dto.getIsHoliday() : 0;
        double temperature = dto.getTemperature() != null ? dto.getTemperature() : 27.0;
        String weather = (dto.getWeather() != null && !dto.getWeather().trim().isEmpty()) ? dto.getWeather().trim() : "Clear";

        Map<String, Object> payload = new HashMap<>();
        payload.put("DayOfWeek", dayOfWeek);
        payload.put("IsWeekend", isWeekend);
        payload.put("IsHoliday", isHoliday);
        payload.put("Temperature", temperature);
        payload.put("Weather", weather);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<DemandForecastPredictionResponseDTO> response = restTemplate.postForEntity(
                    forecastServiceUrl,
                    requestEntity,
                    DemandForecastPredictionResponseDTO.class
            );

            DemandForecastPredictionResponseDTO responseBody = response.getBody();
            if (responseBody != null && targetDate != null) {
                responseBody.setTargetDate(targetDate);
            }
            return responseBody;
        } catch (Exception ex) {
            throw new RuntimeException("Demand Forecasting microservice error: " + ex.getMessage(), ex);
        }
    }

    public DemandForecastDTO addForecast(DemandForecastDTO dto) {
        SystemUsers createdBy = authRepo.findById(dto.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + dto.getCreatedBy()));

        DemandForecast forecast = new DemandForecast();
        forecast.setTemplateId(dto.getTemplateId());
        forecast.setCreatedBy(createdBy);
        forecast.setTargetDate(dto.getTargetDate());
        forecast.setDateDetails(dto.getDateDetails());
        forecast.setTemperature(dto.getTemperature());
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
