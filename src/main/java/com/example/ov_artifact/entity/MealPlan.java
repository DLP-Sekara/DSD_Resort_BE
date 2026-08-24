package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.example.ov_artifact.util.MealStatus;

@Entity
@Table(name = "meal_plans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_id", length = 36, nullable = false, updatable = false)
    private String planId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "plan_code", length = 10, unique = true, nullable = false)
    private String planCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private MealStatus status;

}
