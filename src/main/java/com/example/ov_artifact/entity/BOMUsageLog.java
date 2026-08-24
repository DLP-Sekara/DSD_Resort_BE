package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOMUsageLog")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BOMUsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usage_id", length = 36, nullable = false, updatable = false)
    private String usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false, columnDefinition = "varchar(36)")
    private BOMTemplate bomTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooked_by", nullable = false, columnDefinition = "varchar(36)")
    private SystemUsers cookedBy;

    @Column(name = "usage_date")
    private LocalDateTime usageDate;

    @Column(name = "portions_cooked", nullable = false)
    private Integer portionsCooked;
}
