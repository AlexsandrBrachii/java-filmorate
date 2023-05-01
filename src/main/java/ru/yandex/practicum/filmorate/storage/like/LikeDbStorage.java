package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        final String sql = "insert into likes (film_id, user_id) values (?, ?)";

        try {
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);}
        catch (DuplicateKeyException ignored) {
            log.warn("Пользователь с id = {} уже ставил лайк фильму с id = {}", userId, filmId);
        }
    }

    @Override
    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        final String sql = "delete from likes where film_id = ? and user_id = ?";

        jdbcTemplate.update(sql, filmId, userId);
    }
}
