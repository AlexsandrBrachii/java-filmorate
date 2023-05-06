package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int reviewId);

    Review getReview(int reviewId);

    Collection<Review> getAllReviews(Integer filmId, Integer count);

    void makeLikeReview(int reviewId, int userId);

    void makeDislikeReview(int reviewId, int userId);

    void deleteLikeReview(int reviewId, int userId);

    void deleteDislikeReview(int reviewId, int userId);
}