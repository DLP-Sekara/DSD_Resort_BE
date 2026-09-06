package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RestaurantOrderDetail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "detail_id", length = 36, nullable = false, updatable = false)
    private String detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "varchar(36)")
    private RestaurantOrder restaurantOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "varchar(36)")
    private FoodItem foodItem;

    @Column(name = "ordered_qty", nullable = false)
    private Integer orderedQty;
}
