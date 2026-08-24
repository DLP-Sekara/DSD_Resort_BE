package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "FoodItem")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", length = 36, nullable = false, updatable = false)
    private String itemId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;
}
