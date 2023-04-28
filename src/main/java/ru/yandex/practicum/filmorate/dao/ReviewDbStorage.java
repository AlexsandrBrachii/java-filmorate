package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@RequiredArgsConstructor
@Component
@Primary
@Slf4j
public class ReviewDbStorage implements ReviewStorageDb {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        String sqlInsertReview = "INSERT INTO reviews (content, is_positive, rating_useful, user_id, film_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUseful() == null ? 0 : review.getUseful());
            ps.setInt(4, review.getUserId());
            ps.setInt(5, review.getFilmId());
            return ps;
        }, keyHolder);
        int reviewId = keyHolder.getKey().intValue();
        review.setReviewId(reviewId);
        log.info("Отзыв c id=" + reviewId + " добавлен");
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlUpdateReview = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlUpdateReview,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        log.info("Отзыв c id=" + review.getReviewId() + " обновлён");
        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(int reviewId) {
        String sqlDelete = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sqlDelete, reviewId);
        log.info("Отзыв c id=" + reviewId + " удалён");
    }

    @Override
    public Review getReview(int reviewId) {
        String sqlSelect = "SELECT * FROM reviews WHERE review_id = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlSelect, reviewId);
        Review review = null;
        if (reviewRows.next()) {
            review = Review.builder()
                    .reviewId(reviewId)
                    .content(reviewRows.getString("content"))
                    .isPositive(reviewRows.getBoolean("is_positive"))
                    .useful(reviewRows.getInt("rating_useful"))
                    .userId(reviewRows.getInt("user_id"))
                    .filmId(reviewRows.getInt("film_id")).build();
        }
        return review;
    }

    @Override
    public Collection<Review> getAllReviews(Integer filmId, int count) {
        String sql0;
        String sql1 = "SELECT review_id, content, is_positive, rating_useful, user_id, film_id FROM reviews";
        if (filmId != 0) {
            sql0 = sql1 + " WHERE film_id = ? ORDER BY rating_useful DESC";
        } else {
            sql0 = sql1 + " ORDER BY rating_useful DESC";
        }
        if (count > 0) {
            sql0 += " LIMIT ?";
        }

        return jdbcTemplate.query(sql0, ps -> {
            if (filmId != 0) {
                ps.setInt(1, filmId);
                if (count > 0) {
                    ps.setInt(2, count);
                }
            } else {
                if (count > 0) {
                    ps.setInt(1, count);
                }
            }
        }, (rs, rowNum) -> {
            Review review = Review.builder()
                    .reviewId(rs.getInt("review_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .useful(rs.getInt("rating_useful"))
                    .userId(rs.getInt("user_id"))
                    .filmId(rs.getInt("film_id"))
                    .build();
            return review;
        });
    }


    @Override
    public void makeLikeReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        int rating = review.getUseful();
        review.setUseful(++rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " поставил лайк отзыву с id=" + reviewId);
    }

    @Override
    public void deleteLikeReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        int rating = review.getUseful();
        review.setUseful(--rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " удалил лайк отзыву с id=" + reviewId);
    }

    @Override
    public void makeDislikeReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        int rating = review.getUseful();
        review.setUseful(--rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " поставил дизлайк отзыву с id=" + reviewId);
    }

    @Override
    public void deleteDislikeReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        int rating = review.getUseful();
        review.setUseful(++rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " удалил дизлайк отзыву с id=" + reviewId);
    }

    public void updateUsefulForReview(Review review) {
        String sqlUpdateReview = "UPDATE reviews SET rating_useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlUpdateReview,
                review.getUseful(),
                review.getReviewId());
        log.info("Рейтинг для отзыва с id=" + review.getReviewId() + " обновлён");
    }
}