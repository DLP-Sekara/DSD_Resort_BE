package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.MealPlanDTO;
import com.example.ov_artifact.services.MealPlanService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addMealPlan(@RequestBody MealPlanDTO mealPlanDTO) {
        mealPlanService.addMealPlan(mealPlanDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Meal Plan Added Successfully", null),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateMealPlan(@RequestBody MealPlanDTO mealPlanDTO) {
        mealPlanService.updateMealPlan(mealPlanDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Meal Plan Updated Successfully", null),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteMealPlan(@PathVariable String id) {
        mealPlanService.deleteMealPlan(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Meal Plan Deleted Successfully", null),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllMealPlans() {
        List<MealPlanDTO> mealPlans = mealPlanService.getAllMealPlans();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Meal Plans Fetched Successfully", mealPlans),
                HttpStatus.OK);
    }
}
