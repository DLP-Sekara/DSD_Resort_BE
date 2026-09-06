package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.FoodItemDTO;
import com.example.ov_artifact.services.FoodItemService;
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
@RequestMapping("/api/v1/food-items")
@RequiredArgsConstructor
public class FoodItemController {

    private final FoodItemService foodItemService;

    @PreAuthorize("hasAnyRole('ADMIN','HEAD_CHEF')")
    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addFoodItem(@RequestBody FoodItemDTO foodItemDTO) {
        foodItemService.addFoodItem(foodItemDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Food Item Added Successfully", null),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HEAD_CHEF' )")
    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateFoodItem(@RequestBody FoodItemDTO foodItemDTO) {
        foodItemService.updateFoodItem(foodItemDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Food Item Updated Successfully", null),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteFoodItem(@PathVariable String id) {
        foodItemService.deleteFoodItem(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Food Item Deleted Successfully", null),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'HEAD_CHEF', 'CHEF')")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getFoodItemById(@PathVariable String id) {
        FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Food Item Fetched Successfully", foodItem),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'HEAD_CHEF', 'CHEF')")
    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllFoodItems() {
        List<FoodItemDTO> foodItems = foodItemService.getAllFoodItems();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Food Items Fetched Successfully", foodItems),
                HttpStatus.OK);
    }
}
