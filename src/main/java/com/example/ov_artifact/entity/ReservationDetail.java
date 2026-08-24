package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "detail_id", length = 36, nullable = false, updatable = false)
    private String detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "res_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "ordered_qty", nullable = false)
    private Integer orderedQty;
}
