package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.BOMTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BOMTemplateRepository extends JpaRepository<BOMTemplate, String> {
    List<BOMTemplate> findByCreatedBy_User_id(String userId);
}
