package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.BOMUsageLogDTO;
import com.example.ov_artifact.dto.UsedBOMItemDTO;
import com.example.ov_artifact.entity.BOMTemplate;
import com.example.ov_artifact.entity.BOMTemplateItem;
import com.example.ov_artifact.entity.BOMUsageLog;
import com.example.ov_artifact.entity.RawMaterial;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.BOMTemplateItemRepository;
import com.example.ov_artifact.repository.BOMTemplateRepository;
import com.example.ov_artifact.repository.BOMUsageLogRepository;
import com.example.ov_artifact.repository.RawMaterialRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BOMUsageLogService {

    private final BOMUsageLogRepository usageLogRepository;
    private final BOMTemplateRepository bomTemplateRepository;
    private final BOMTemplateItemRepository bomTemplateItemRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final AuthRepo authRepo;

    public List<BOMUsageLogDTO> logUsageBatch(BOMUsageLogDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        if (request.getCookedBy() == null || request.getCookedBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Cooked By (User ID) is required.");
        }

        SystemUsers cookedBy = authRepo.findById(request.getCookedBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + request.getCookedBy()));

        List<UsedBOMItemDTO> usedList = request.getUsedBOMs();
        if (usedList == null || usedList.isEmpty()) {
            if (request.getTemplateId() != null && request.getPortionsCooked() != null) {
                UsedBOMItemDTO single = new UsedBOMItemDTO(request.getTemplateId(), null, request.getPortionsCooked());
                usedList = List.of(single);
            } else {
                throw new IllegalArgumentException("Used_BOMs list cannot be empty.");
            }
        }

        List<BOMUsageLogDTO> savedDTOs = new ArrayList<>();

        for (UsedBOMItemDTO usedItem : usedList) {
            BOMTemplate template = null;
            if (usedItem.getTemplateId() != null && !usedItem.getTemplateId().trim().isEmpty()) {
                template = bomTemplateRepository.findById(usedItem.getTemplateId())
                        .orElse(null);
            }

            if (template == null && usedItem.getItemId() != null && !usedItem.getItemId().trim().isEmpty()) {
                template = bomTemplateRepository.findByFoodItem_ItemId(usedItem.getItemId())
                        .orElse(null);
            }

            if (template == null) {
                throw new RuntimeException("BOM Template not found for templateId: " + usedItem.getTemplateId() + " or itemId: " + usedItem.getItemId());
            }

            int portions = (usedItem.getPortionsCooked() != null && usedItem.getPortionsCooked() > 0) ? usedItem.getPortionsCooked() : 1;

            BOMUsageLog usageLog = new BOMUsageLog();
            usageLog.setBomTemplate(template);
            usageLog.setCookedBy(cookedBy);
            usageLog.setUsageDate(LocalDateTime.now());
            usageLog.setPortionsCooked(portions);

            BOMUsageLog savedLog = usageLogRepository.save(usageLog);

            // Deduct raw materials from warehouse stock
            List<BOMTemplateItem> templateItems = bomTemplateItemRepository.findByBomTemplate_TemplateId(template.getTemplateId());
            for (BOMTemplateItem item : templateItems) {
                if (item.getRawMaterial() != null && item.getQtyPerPerson() != null) {
                    RawMaterial material = rawMaterialRepository.findById(item.getRawMaterial().getMaterialId())
                            .orElse(item.getRawMaterial());
                    BigDecimal requiredQty = item.getQtyPerPerson().multiply(BigDecimal.valueOf(portions));
                    BigDecimal currentStock = material.getQuantityOnHand() != null ? material.getQuantityOnHand() : BigDecimal.ZERO;
                    BigDecimal newStock = currentStock.subtract(requiredQty);
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        newStock = BigDecimal.ZERO;
                    }
                    material.setQuantityOnHand(newStock);
                    rawMaterialRepository.save(material);
                }
            }

            savedDTOs.add(mapToDTO(savedLog));
        }

        return savedDTOs;
    }

    public BOMUsageLogDTO getUsageLogById(String usageId) {
        BOMUsageLog usageLog = usageLogRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("BOMUsageLog not found with ID: " + usageId));
        return mapToDTO(usageLog);
    }

    public List<BOMUsageLogDTO> getUsageLogsByTemplate(String templateId) {
        List<BOMUsageLog> logs = usageLogRepository.findByBomTemplate_TemplateId(templateId);
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<BOMUsageLogDTO> getAllUsageLogs() {
        List<BOMUsageLog> logs = usageLogRepository.findAll();
        return logs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private BOMUsageLogDTO mapToDTO(BOMUsageLog log) {
        BOMUsageLogDTO dto = new BOMUsageLogDTO();
        dto.setUsageId(log.getUsageId());
        if (log.getBomTemplate() != null) {
            dto.setTemplateId(log.getBomTemplate().getTemplateId());
            dto.setTemplateName(log.getBomTemplate().getTemplateName());
            if (log.getBomTemplate().getFoodItem() != null) {
                dto.setItemId(log.getBomTemplate().getFoodItem().getItemId());
                dto.setItemName(log.getBomTemplate().getFoodItem().getName());
            }
        }
        if (log.getCookedBy() != null) {
            dto.setCookedBy(log.getCookedBy().getUserId());
            dto.setCookerName(log.getCookedBy().getName());
        }
        dto.setUsageDate(log.getUsageDate());
        dto.setPortionsCooked(log.getPortionsCooked());
        return dto;
    }
}
