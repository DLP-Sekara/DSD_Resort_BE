package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "RawMaterial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "material_id", length = 36, nullable = false, updatable = false)
    private String materialId;

    @Column(name = "material_name", length = 100, nullable = false)
    private String materialName;

    @Column(name = "unit_of_measure", length = 20, nullable = false)
    private String unitOfMeasure;

    @Column(name = "quantity_on_hand", precision = 10, scale = 2)
    private BigDecimal quantityOnHand;

    @Column(name = "category", length = 50)
    private String category;
}
