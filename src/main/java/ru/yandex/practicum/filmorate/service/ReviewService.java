package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.EventOperation;
import ru.yandex.practicum.filmorate.constants.EventType;
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
    private final EventService eventService;

    public Review addReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        if (review.getIsPositive() == null || review.getContent() == null) {
            throw new ValidationException("Поле isPositive не может быть null");
        }
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.ADD, review.getReviewId());
        return reviewStorageDb.addReview(review);
    }

    public Review updateReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getReviewId());
        return reviewStorageDb.updateReview(review);
    }

    public void deleteReview(int reviewId) {
        reviewStorageDb.deleteReview(reviewId);
        Review review = reviewStorageDb.getReview(reviewId);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
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
