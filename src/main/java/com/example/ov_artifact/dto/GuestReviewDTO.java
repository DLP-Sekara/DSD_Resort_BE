package com.example.ov_artifact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    private Integer starRating;

    @JsonProperty("food_items")
    private List<String> foodItems;

    @JsonProperty("members")
    private List<String> staffMembers;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy.M.d")
    private LocalDate dateOfVisit;
}
