package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.RawMaterialDTO;
import com.example.ov_artifact.entity.RawMaterial;
import com.example.ov_artifact.repository.RawMaterialRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final ModelMapper modelMapper;

    public RawMaterialDTO addRawMaterial(RawMaterialDTO rawMaterialDTO) {
        if (rawMaterialRepository.existsByMaterialName(rawMaterialDTO.getMaterialName())) {
            throw new IllegalArgumentException("Material name '" + rawMaterialDTO.getMaterialName() + "' already exists!");
        }
        RawMaterial rawMaterial = modelMapper.map(rawMaterialDTO, RawMaterial.class);
        RawMaterial savedMaterial = rawMaterialRepository.save(rawMaterial);
        return modelMapper.map(savedMaterial, RawMaterialDTO.class);
    }

    public void updateRawMaterial(RawMaterialDTO rawMaterialDTO) {
        if (rawMaterialRepository.existsById(rawMaterialDTO.getMaterialId())) {
            RawMaterial rawMaterial = modelMapper.map(rawMaterialDTO, RawMaterial.class);
            rawMaterialRepository.save(rawMaterial);
        } else {
            throw new RuntimeException("Raw Material not found for ID: " + rawMaterialDTO.getMaterialId());
        }
    }

    public void deleteRawMaterial(String id) {
        if (rawMaterialRepository.existsById(id)) {
            rawMaterialRepository.deleteById(id);
        } else {
            throw new RuntimeException("Raw Material not found for ID: " + id);
        }
    }

    public RawMaterialDTO getRawMaterialById(String id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raw Material not found for ID: " + id));
        return modelMapper.map(rawMaterial, RawMaterialDTO.class);
    }

    public List<RawMaterialDTO> getAllRawMaterials() {
        List<RawMaterial> rawMaterials = rawMaterialRepository.findAll();
        return modelMapper.map(rawMaterials, new TypeToken<List<RawMaterialDTO>>() {
        }.getType());
    }
}
