package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.GuestReviewDTO;
import com.example.ov_artifact.services.GuestReviewService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/guest-reviews")
@RequiredArgsConstructor
public class GuestReviewController {

    private final GuestReviewService guestReviewService;

    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addReview(@RequestBody GuestReviewDTO dto) {
        GuestReviewDTO savedReview = guestReviewService.addReview(dto);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Guest Review Added Successfully", savedReview),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getReviewById(@PathVariable String id) {
        GuestReviewDTO review = guestReviewService.getReviewById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Review Fetched Successfully", review),
                HttpStatus.OK);
    }

    @GetMapping("/by-guest/{guestId}")
    public ResponseEntity<StandardResponse> getReviewsByGuest(@PathVariable String guestId) {
        List<GuestReviewDTO> reviews = guestReviewService.getReviewsByGuest(guestId);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Reviews Fetched Successfully", reviews),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllReviews() {
        List<GuestReviewDTO> reviews = guestReviewService.getAllReviews();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Reviews Fetched Successfully", reviews),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteReview(@PathVariable String id) {
        guestReviewService.deleteReview(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Review Deleted Successfully", null),
                HttpStatus.OK);
    }
}
