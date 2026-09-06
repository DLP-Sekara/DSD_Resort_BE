package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.GuestReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestReviewRepository extends JpaRepository<GuestReview, String> {
    List<GuestReview> findByGuest_GuestId(String guestId);
}
