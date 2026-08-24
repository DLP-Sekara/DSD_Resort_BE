package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestReviewDTO {
    private String reviewId;
    private String guestId;
    private String resId;
    private String orderId;
    private String reviewText;
    private BigDecimal nlpScore;
    private String sentimentLabel;
}
