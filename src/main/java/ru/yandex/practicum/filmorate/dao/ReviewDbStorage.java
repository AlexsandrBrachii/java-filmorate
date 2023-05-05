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
            ps.setInt(3, review.getUseful() == null ? 0 : calculateReviewUseful(keyHolder.getKey().intValue()));
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
    public Collection<Review> getAllReviews(Integer filmId, Integer count) {
        StringBuilder sqlQuery = new StringBuilder("SELECT review_id, content, is_positive, rating_useful, user_id, film_id FROM reviews");

        if (filmId != null) {
            sqlQuery.append(" WHERE film_id = ?");
        }

        sqlQuery.append(" ORDER BY rating_useful DESC");

        if (count != null && count > 0) {
            sqlQuery.append(" LIMIT ?");
        }

        return jdbcTemplate.query(sqlQuery.toString(), ps -> {
            int parameterIndex = 1;
            if (filmId != null) {
                ps.setInt(parameterIndex++, filmId);
            }
            if (count != null && count > 0) {
                ps.setInt(parameterIndex, count);
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
        String sqlInsert = "INSERT INTO review_useful (review_id, rating, user_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsert, reviewId, "like", userId);
        Review review = getReview(reviewId);
        int rating = calculateReviewUseful(reviewId);
        review.setUseful(rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " поставил лайк отзыву с id=" + reviewId);
    }

    @Override
    public void deleteLikeReview(int reviewId, int userId) {
        String sqlDelete = "DELETE FROM review_useful WHERE user_id = ? AND rating = ?";
        jdbcTemplate.update(sqlDelete, userId, "like");
        Review review = getReview(reviewId);
        int rating = calculateReviewUseful(reviewId);
        review.setUseful(rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " удалил лайк отзыву с id=" + reviewId);
    }

    @Override
    public void makeDislikeReview(int reviewId, int userId) {
        String sqlInsert = "INSERT INTO review_useful (review_id, rating, user_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsert, reviewId, "dislike", userId);
        Review review = getReview(reviewId);
        int rating = calculateReviewUseful(reviewId);;
        review.setUseful(rating);
        updateUsefulForReview(review);
        log.info("Пользователь с id=" + userId + " поставил дизлайк отзыву с id=" + reviewId);
    }

    @Override
    public void deleteDislikeReview(int reviewId, int userId) {
        String sqlDelete = "DELETE FROM review_useful WHERE user_id = ? AND rating = ?";
        jdbcTemplate.update(sqlDelete, userId, "dislike");
        Review review = getReview(reviewId);
        int rating = calculateReviewUseful(reviewId);
        review.setUseful(rating);
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

    public Integer calculateReviewUseful(int reviewId) {
        Integer rating;
        String sql = "SELECT COUNT(*) FROM review_useful WHERE review_id = ? AND rating = ?";
        Integer likes = jdbcTemplate.queryForObject(sql, new Object[]{reviewId, "like"}, Integer.class);
        Integer dislikes = jdbcTemplate.queryForObject(sql, new Object[]{reviewId, "dislike"}, Integer.class);
        if (likes != null & dislikes != null) {
            rating = likes - dislikes;
        } else if (likes == null && dislikes != null) {
            rating = -dislikes;
        } else if (likes != null && dislikes == null) {
            rating = likes;
        } else {
            rating = 0;
        }
        return rating;
    }
}