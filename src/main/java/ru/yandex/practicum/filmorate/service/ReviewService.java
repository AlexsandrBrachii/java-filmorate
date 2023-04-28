package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewStorageDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewStorageDb reviewStorageDb;
    private final UserService userService;
    private final FilmService filmService;

    public Review addReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        if (review.getIsPositive() == null || review.getContent() == null) {
            throw new ValidationException("Поле isPositive не может быть null");
        }
        return reviewStorageDb.addReview(review);
    }

    public Review updateReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        return reviewStorageDb.updateReview(review);
    }

    public void deleteReview(int reviewId) {
        reviewStorageDb.deleteReview(reviewId);
    }

    public Review getReview(int reviewId) {
        Review review = reviewStorageDb.getReview(reviewId);
        if (review == null) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден");
        }
        return review;
    }

    public Collection<Review> getAllReviews(Integer filmId, int count) {
        return reviewStorageDb.getAllReviews(filmId, count);
    }

    public void makeLikeReview(int reviewId, int userId) {
        reviewStorageDb.makeLikeReview(reviewId, userId);
    }

    public void makeDislikeReview(int reviewId, int userId) {
        reviewStorageDb.makeDislikeReview(reviewId, userId);
    }

    public void deleteLikeReview(int reviewId, int userId) {
        reviewStorageDb.deleteLikeReview(reviewId, userId);
    }

    public void deleteDislikeReview(int reviewId, int userId) {
        reviewStorageDb.deleteDislikeReview(reviewId, userId);
    }
}
