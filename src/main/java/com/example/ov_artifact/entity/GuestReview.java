package com.example.ov_artifact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "GuestReview")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", length = 36, nullable = false, updatable = false)
    private String reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "res_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private RestaurantOrder restaurantOrder;

    @Column(name = "review_text", columnDefinition = "TEXT", nullable = false)
    private String reviewText;

    @Column(name = "nlp_score", precision = 5, scale = 2)
    private BigDecimal nlpScore;

    @Column(name = "sentiment_label", length = 20)
    private String sentimentLabel;
}
