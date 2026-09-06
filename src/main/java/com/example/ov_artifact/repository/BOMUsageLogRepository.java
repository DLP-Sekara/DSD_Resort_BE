package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.BOMUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BOMUsageLogRepository extends JpaRepository<BOMUsageLog, String> {
    List<BOMUsageLog> findByBomTemplate_TemplateId(String templateId);
}
