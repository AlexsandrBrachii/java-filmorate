package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int reviewId);

    Review getReview(int reviewId);

    Collection<Review> getAllReviews(Integer filmId, Integer count);

    void makeLikeOrDislike(int reviewId, int userId, boolean grade);

    void deleteLikeOrDislike(int reviewId, int userId, boolean grade);

}