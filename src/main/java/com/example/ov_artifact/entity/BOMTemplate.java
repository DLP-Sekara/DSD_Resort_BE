package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOMTemplate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_id", length = 36, nullable = false, updatable = false)
    private String templateId;

    @Column(name = "template_name", length = 100, nullable = false)
    private String templateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, columnDefinition = "varchar(36)")
    private SystemUsers createdBy;
}
