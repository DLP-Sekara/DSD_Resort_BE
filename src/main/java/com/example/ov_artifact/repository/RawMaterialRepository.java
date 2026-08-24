package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, String> {
    boolean existsByMaterialName(String materialName);
}
