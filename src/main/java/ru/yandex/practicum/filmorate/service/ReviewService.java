package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event_enum.EventOperation;
import ru.yandex.practicum.filmorate.dao.event_enum.EventType;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewStorage reviewStorageDb;
    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

     public Review addReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        Review r = reviewStorageDb.addReview(review);
        eventService.createEvent(r.getUserId(), EventType.REVIEW, EventOperation.ADD, r.getReviewId());
        return r;
    }

   public Review updateReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        Review r = reviewStorageDb.updateReview(review);
        eventService.createEvent(r.getUserId(), EventType.REVIEW, EventOperation.UPDATE, r.getReviewId());
        return r;
    }

    public void deleteReview(int reviewId) {
        Review review = reviewStorageDb.getReview(reviewId);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
        reviewStorageDb.deleteReview(reviewId);
    }

    public Review getReview(int reviewId) {
        Review review = reviewStorageDb.getReview(reviewId);
        if (review == null) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден");
        }
        return review;
    }

    public Collection<Review> getAllReviews(Integer filmId, Integer count) {
        return reviewStorageDb.getAllReviews(filmId, count);
    }

    public void makeLikeReview(int reviewId, int userId) {
        reviewStorageDb.makeLikeOrDislike(reviewId, userId, true);
    }

    public void makeDislikeReview(int reviewId, int userId) {
        reviewStorageDb.makeLikeOrDislike(reviewId, userId, false);
    }

    public void deleteLikeReview(int reviewId, int userId) {
        reviewStorageDb.deleteLikeOrDislike(reviewId, userId, true);
    }

    public void deleteDislikeReview(int reviewId, int userId) {
        reviewStorageDb.deleteLikeOrDislike(reviewId, userId, false);
    }
}
