package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface MealPlanRepository extends JpaRepository<MealPlan, String> {

    boolean existsByName(String name);
}
