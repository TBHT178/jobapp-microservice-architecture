/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tramtbh.reviewms.review;

import com.tramtbh.reviewms.review.messaging.ReviewMessageProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author DELL
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;

    private ReviewMessageProducer reviewMessageProducer;

    public ReviewController(ReviewService reviewService, ReviewMessageProducer reviewMessageProducer) {
        this.reviewService = reviewService;
        this.reviewMessageProducer = reviewMessageProducer;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId) {
        return new ResponseEntity<>(reviewService.getAllReviews(companyId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestParam Long companyId, @RequestBody Review review) {
        Boolean isReviewSaved = reviewService.addReview(companyId, review);
        if (isReviewSaved) {
            reviewMessageProducer.sendMessage(review);
            return new ResponseEntity<>("Review Added!", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Can not add review!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview( @PathVariable Long reviewId) {
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview( @PathVariable Long reviewId, @RequestBody Review updateReview) {
        boolean isReviewUpdate = reviewService.updateReview(reviewId, updateReview);
        if (isReviewUpdate) {
            return new ResponseEntity<>("Review has been updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Can not update review!", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview( @PathVariable Long reviewId) {
        boolean isReviewDeleted = reviewService.deleteReview( reviewId);
        if (isReviewDeleted) {
            return new ResponseEntity<>("Review has been deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Can not delete review!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/averageRating")
    public Double getAverageRating(@RequestParam Long companyId) {
        List<Review> reviewList = reviewService.getAllReviews(companyId);
        return reviewList.stream().mapToDouble(Review::getRating).average().orElse(0.0);
    }
}
