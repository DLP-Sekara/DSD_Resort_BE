package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.FoodItemDTO;
import com.example.ov_artifact.entity.FoodItem;
import com.example.ov_artifact.repository.FoodItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final ModelMapper modelMapper;

    public void addFoodItem(FoodItemDTO foodItemDTO) {
        if (foodItemRepository.existsByName(foodItemDTO.getName())) {
            throw new IllegalArgumentException("Food Item name " + foodItemDTO.getName() + "' already exists!");
        }
        FoodItem foodItem = modelMapper.map(foodItemDTO, FoodItem.class);
        foodItemRepository.save(foodItem);
    }

    public void updateFoodItem(FoodItemDTO foodItemDTO) {
        if (foodItemRepository.existsById(foodItemDTO.getItemId())) {
            FoodItem foodItem = modelMapper.map(foodItemDTO, FoodItem.class);
            foodItemRepository.save(foodItem);
        } else {
            throw new RuntimeException("Food Item not found for ID: " + foodItemDTO.getItemId());
        }
    }

    public void deleteFoodItem(String id) {
        if (foodItemRepository.existsById(id)) {
            foodItemRepository.deleteById(id);
        } else {
            throw new RuntimeException("Food Item not found for ID: " + id);
        }
    }

    public List<FoodItemDTO> getAllFoodItems() {
        List<FoodItem> foodItems = foodItemRepository.findAll();
        return modelMapper.map(foodItems, new TypeToken<List<FoodItemDTO>>() {
        }.getType());
    }
}
