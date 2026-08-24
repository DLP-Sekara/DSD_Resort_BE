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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestReviewService {

    private final GuestReviewRepository reviewRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final ModelMapper modelMapper;

    public GuestReviewDTO addReview(GuestReviewDTO dto) {
        Guest guest = guestRepository.findById(dto.getGuestId())
                .orElseThrow(() -> new RuntimeException("Guest not found with ID: " + dto.getGuestId()));

        GuestReview review = new GuestReview();
        review.setGuest(guest);
        review.setReviewText(dto.getReviewText());
        review.setNlpScore(dto.getNlpScore());
        review.setSentimentLabel(dto.getSentimentLabel());

        if (dto.getResId() != null && !dto.getResId().isEmpty()) {
            Reservation reservation = reservationRepository.findById(dto.getResId()).orElse(null);
            review.setReservation(reservation);
        }

        if (dto.getOrderId() != null && !dto.getOrderId().isEmpty()) {
            RestaurantOrder order = restaurantOrderRepository.findById(dto.getOrderId()).orElse(null);
            review.setRestaurantOrder(order);
        }

        GuestReview savedReview = reviewRepository.save(review);
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
