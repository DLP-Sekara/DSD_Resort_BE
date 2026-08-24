package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "room_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "type_id", length = 36, nullable = false, updatable = false)
    private String typeId;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "price_per_night", precision = 10, scale = 2, nullable = false)
    private BigDecimal pricePerNight;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;
}
