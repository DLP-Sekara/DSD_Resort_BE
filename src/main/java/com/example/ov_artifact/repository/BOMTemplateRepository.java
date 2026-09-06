package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.BOMTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BOMTemplateRepository extends JpaRepository<BOMTemplate, String> {
    List<BOMTemplate> findByCreatedBy_UserId(String userId);
    Optional<BOMTemplate> findByFoodItem_ItemId(String itemId);
    boolean existsByFoodItem_ItemId(String itemId);
    boolean existsByFoodItem_ItemIdAndTemplateIdNot(String itemId, String templateId);
}
