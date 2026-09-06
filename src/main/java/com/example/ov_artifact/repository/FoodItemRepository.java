package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, String> {

    boolean existsByName(String name);
}
