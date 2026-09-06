package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.BOMTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BOMTemplateItemRepository extends JpaRepository<BOMTemplateItem, String> {
    List<BOMTemplateItem> findByBomTemplate_TemplateId(String templateId);
}
