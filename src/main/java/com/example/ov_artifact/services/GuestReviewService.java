package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.GuestReviewDTO;
import com.example.ov_artifact.entity.Guest;
import com.example.ov_artifact.entity.GuestReview;
import com.example.ov_artifact.entity.Reservation;
import com.example.ov_artifact.entity.RestaurantOrder;
import com.example.ov_artifact.repository.GuestRepository;
import com.example.ov_artifact.repository.GuestReviewRepository;
import com.example.ov_artifact.repository.ReservationRepository;
import com.example.ov_artifact.repository.RestaurantOrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestReviewService {

    private final GuestReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${nlp.service.url:http://127.0.0.1:8000/api/v1/nlp/analyze}")
    private String nlpServiceUrl;

    public GuestReviewDTO addReview(GuestReviewDTO dto) {
        GuestReview review = new GuestReview();
        Guest guest = null;

        if (dto.getResId() != null && !dto.getResId().trim().isEmpty() && !dto.getResId().equalsIgnoreCase("null")) {
            Reservation reservation = reservationRepository.findById(dto.getResId()).orElse(null);
            review.setReservation(reservation);
            if (reservation != null) {
                guest = reservation.getGuest();
            }
        }

        if (dto.getOrderId() != null && !dto.getOrderId().trim().isEmpty() && !dto.getOrderId().equalsIgnoreCase("null")) {
            RestaurantOrder order = restaurantOrderRepository.findById(dto.getOrderId()).orElse(null);
            review.setRestaurantOrder(order);
            if (guest == null && order != null) {
                guest = order.getGuest();
            }
        }

        review.setGuest(guest);
        review.setReviewText(dto.getReviewText());
        review.setStarRating(dto.getStarRating());
        review.setFoodItems(dto.getFoodItems());
        review.setStaffMembers(dto.getStaffMembers());
        review.setDateOfVisit(dto.getDateOfVisit());

        // Call NLP API to get sentiment and score
        if (dto.getReviewText() != null && !dto.getReviewText().isEmpty()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("review_text", dto.getReviewText());
                
                HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(nlpServiceUrl, request, Map.class);
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    if (body.get("sentiment_label") != null) {
                        review.setSentimentLabel(body.get("sentiment_label").toString());
                    }
                    if (body.get("confidence_score") != null) {
                        review.setNlpScore(new BigDecimal(body.get("confidence_score").toString()));
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to call NLP service: " + e.getMessage());
                review.setNlpScore(dto.getNlpScore());
                review.setSentimentLabel(dto.getSentimentLabel());
            }
        } else {
            review.setNlpScore(dto.getNlpScore());
            review.setSentimentLabel(dto.getSentimentLabel());
        }



        GuestReview savedReview = reviewRepository.save(review);
        messagingTemplate.convertAndSend("/topic/reviews", savedReview);
        return modelMapper.map(savedReview, GuestReviewDTO.class);
    }

    public GuestReviewDTO getReviewById(String reviewId) {
        GuestReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("GuestReview not found with ID: " + reviewId));
        return modelMapper.map(review, GuestReviewDTO.class);
    }

    public List<GuestReviewDTO> getReviewsByGuest(String guestId) {
        List<GuestReview> reviews = reviewRepository.findByGuest_GuestId(guestId);
        return modelMapper.map(reviews, new TypeToken<List<GuestReviewDTO>>() {}.getType());
    }

    public List<GuestReviewDTO> getAllReviews() {
        List<GuestReview> reviews = reviewRepository.findAll();
        return modelMapper.map(reviews, new TypeToken<List<GuestReviewDTO>>() {}.getType());
    }

    public void deleteReview(String reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("GuestReview not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}
