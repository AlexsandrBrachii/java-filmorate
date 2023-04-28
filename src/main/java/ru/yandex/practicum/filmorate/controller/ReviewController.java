package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(value = "/{reviewId}")
    private Review getReview(@PathVariable int reviewId) {
        return reviewService.getReview(reviewId);
    }

    @GetMapping
    private Collection<Review> getAllReviews(@RequestParam(defaultValue = "0") Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PostMapping
    private Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    private Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping(value = "/{reviewId}")
    private void deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @PutMapping(value = "/{reviewId}/like/{userId}")
    private void makeLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.makeLikeReview(reviewId, userId);
    }

    @PutMapping(value = "/{reviewId}/dislike/{userId}")
    private void makeDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.makeDislikeReview(reviewId, userId);
    }

    @DeleteMapping(value = "/{reviewId}/like/{userId}")
    private void deleteLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping(value = "/{reviewId}/dislike/{userId}")
    private void deleteDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.deleteDislikeReview(reviewId, userId);
    }
}