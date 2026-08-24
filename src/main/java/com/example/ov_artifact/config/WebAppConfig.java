package com.example.ov_artifact.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.ov_artifact.dto.MealPlanDTO;
import com.example.ov_artifact.entity.MealPlan;
import com.example.ov_artifact.util.MealStatus;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5175")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // String to Enum (DTO -> Entity)
        modelMapper.createTypeMap(MealPlanDTO.class, MealPlan.class).addMappings(mapper -> {
            mapper.using(ctx -> ctx.getSource() == null ? null
                    : MealStatus.valueOf(ctx.getSource().toString().toUpperCase()))
                    .map(MealPlanDTO::getStatus, MealPlan::setStatus);
        });

        // Enum to String (Entity -> DTO)
        modelMapper.createTypeMap(MealPlan.class, MealPlanDTO.class).addMappings(mapper -> {
            mapper.using(ctx -> ctx.getSource() == null ? null : ctx.getSource().toString())
                    .map(MealPlan::getStatus, MealPlanDTO::setStatus);
        });

        return modelMapper;
    }
}
