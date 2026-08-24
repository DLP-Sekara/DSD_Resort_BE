package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.BOMTemplateDTO;
import com.example.ov_artifact.dto.BOMTemplateItemDTO;
import com.example.ov_artifact.entity.BOMTemplate;
import com.example.ov_artifact.entity.BOMTemplateItem;
import com.example.ov_artifact.entity.RawMaterial;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.BOMTemplateItemRepository;
import com.example.ov_artifact.repository.BOMTemplateRepository;
import com.example.ov_artifact.repository.RawMaterialRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BOMTemplateService {

    private final BOMTemplateRepository bomTemplateRepository;
    private final BOMTemplateItemRepository bomTemplateItemRepository;
    private final AuthRepo authRepo;
    private final RawMaterialRepository rawMaterialRepository;
    private final ModelMapper modelMapper;

    public BOMTemplateDTO createTemplate(BOMTemplateDTO dto) {
        SystemUsers user = authRepo.findById(dto.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + dto.getCreatedBy()));

        BOMTemplate template = new BOMTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setCreatedBy(user);

        BOMTemplate savedTemplate = bomTemplateRepository.save(template);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (BOMTemplateItemDTO itemDTO : dto.getItems()) {
                RawMaterial material = rawMaterialRepository.findById(itemDTO.getMaterialId())
                        .orElseThrow(() -> new RuntimeException("Raw Material not found with ID: " + itemDTO.getMaterialId()));

                BOMTemplateItem item = new BOMTemplateItem();
                item.setBomTemplate(savedTemplate);
                item.setRawMaterial(material);
                item.setQtyPerPerson(itemDTO.getQtyPerPerson());
                bomTemplateItemRepository.save(item);
            }
        }

        return getTemplateById(savedTemplate.getTemplateId());
    }

    public BOMTemplateDTO getTemplateById(String templateId) {
        BOMTemplate template = bomTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found with ID: " + templateId));

        BOMTemplateDTO dto = modelMapper.map(template, BOMTemplateDTO.class);
        List<BOMTemplateItem> items = bomTemplateItemRepository.findByBomTemplate_TemplateId(templateId);
        dto.setItems(modelMapper.map(items, new TypeToken<List<BOMTemplateItemDTO>>() {}.getType()));

        return dto;
    }

    public List<BOMTemplateDTO> getAllTemplates() {
        List<BOMTemplate> templates = bomTemplateRepository.findAll();
        List<BOMTemplateDTO> dtos = new ArrayList<>();
        for (BOMTemplate t : templates) {
            dtos.add(getTemplateById(t.getTemplateId()));
        }
        return dtos;
    }

    public void deleteTemplate(String templateId) {
        if (!bomTemplateRepository.existsById(templateId)) {
            throw new RuntimeException("BOMTemplate not found with ID: " + templateId);
        }
        bomTemplateRepository.deleteById(templateId);
    }
}
