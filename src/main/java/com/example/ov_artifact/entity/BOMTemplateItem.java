package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "BOMTemplateItem")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMTemplateItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_item_id", length = 36, nullable = false, updatable = false)
    private String templateItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private BOMTemplate bomTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "qty_per_person", precision = 10, scale = 4, nullable = false)
    private BigDecimal qtyPerPerson;
}
