package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.BOMUsageLogDTO;
import com.example.ov_artifact.entity.BOMTemplate;
import com.example.ov_artifact.entity.BOMUsageLog;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.BOMTemplateRepository;
import com.example.ov_artifact.repository.BOMUsageLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BOMUsageLogService {

    private final BOMUsageLogRepository usageLogRepository;
    private final BOMTemplateRepository bomTemplateRepository;
    private final AuthRepo authRepo;
    private final ModelMapper modelMapper;

    public BOMUsageLogDTO logUsage(BOMUsageLogDTO dto) {
        BOMTemplate template = bomTemplateRepository.findById(dto.getTemplateId())
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found with ID: " + dto.getTemplateId()));

        SystemUsers cookedBy = authRepo.findById(dto.getCookedBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + dto.getCookedBy()));

        BOMUsageLog usageLog = new BOMUsageLog();
        usageLog.setBomTemplate(template);
        usageLog.setCookedBy(cookedBy);
        usageLog.setUsageDate(dto.getUsageDate() != null ? dto.getUsageDate() : LocalDateTime.now());
        usageLog.setPortionsCooked(dto.getPortionsCooked());

        BOMUsageLog savedLog = usageLogRepository.save(usageLog);
        return modelMapper.map(savedLog, BOMUsageLogDTO.class);
    }

    public BOMUsageLogDTO getUsageLogById(String usageId) {
        BOMUsageLog usageLog = usageLogRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("BOMUsageLog not found with ID: " + usageId));
        return modelMapper.map(usageLog, BOMUsageLogDTO.class);
    }

    public List<BOMUsageLogDTO> getUsageLogsByTemplate(String templateId) {
        List<BOMUsageLog> logs = usageLogRepository.findByBomTemplate_TemplateId(templateId);
        return modelMapper.map(logs, new TypeToken<List<BOMUsageLogDTO>>() {}.getType());
    }

    public List<BOMUsageLogDTO> getAllUsageLogs() {
        List<BOMUsageLog> logs = usageLogRepository.findAll();
        return modelMapper.map(logs, new TypeToken<List<BOMUsageLogDTO>>() {}.getType());
    }
}
