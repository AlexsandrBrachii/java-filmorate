package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorageDb {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(int id) {
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        user = null;
        if (userRows.next()) {
            user = User.builder().id(id)
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthday").toLocalDate()).build();
        }
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");
        Collection<User> users = new ArrayList<>();
        while (userRows.next()) {
            User user = User.builder().id(userRows.getInt("user_id"))
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthday").toLocalDate()).build();
            users.add(user);
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Integer id = keyHolder.getKey().intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public String deleteUser(int userId) {
        try {
            String sqlQuery = "DELETE  FROM FRIENDS WHERE USER_ID =? OR FRIEND_ID =?;" +
                    "DELETE  FROM LIKES WHERE USER_ID =?;" +
                    "DELETE  FROM USERS WHERE USER_ID =?;";

            jdbcTemplate.update(sqlQuery, userId, userId, userId, userId);
            log.info("Пользователь " + userId + " удалён.");
        } catch (DataAccessException e) {
            log.debug("Пользователь " + userId + " не удалён/ не найден в Базе.");
            throw new NotFoundException("пользователь с id " + userId + " не может быть удалён");
        }
        return "Пользователь " + userId + " удалён.";
    }

    @Override
    public void addFriend(int idUser, int idFriend) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, idUser, idFriend);
        log.info("Вы в друзьях у друга с id=" + idFriend);
    }

    @Override
    public void deleteFriend(int idUser, int idFriend) {
        String sql = "delete from friends where user_id=? and friend_id=?";
        jdbcTemplate.update(sql, idUser, idFriend);
        log.info("user с id=" + idFriend + " удалён из друзей user с id=" + idUser);
    }

    @Override
    public List<User> getFriends(int idUser) {
        if (getUser(idUser) == null) {
            log.debug("пользователь с id " + idUser + " не найден в базе данных.");
            throw new NotFoundException("пользователь с id " + idUser + " не найден");
        }
        String sql = "select u.* from friends f join users u on f.friend_id = u.user_id where f.user_id=?";
        return jdbcTemplate.query(sql, new Object[]{idUser},
                (resultSet, i) -> User.builder().id(resultSet.getInt("user_id"))
                        .email(resultSet.getString("email"))
                        .login(resultSet.getString("login"))
                        .name(resultSet.getString("name"))
                        .birthday(resultSet.getDate("birthday").toLocalDate())
                        .build());
    }

    @Override
    public List<Integer> getRecommendations(int userId) {
        Map<User, List<Integer>> usersWithLikes = new HashMap<>();
        String sql = "SELECT U1.*, L1.film_id, COUNT(*) AS likes_intersection " +
                "FROM likes L1 " +
                "JOIN likes L2 ON L1.film_id = L2.film_id " +
                "JOIN users U1 ON L1.user_id = U1.user_id " +
                "JOIN users U2 ON L2.user_id = U2.user_id  " +
                "GROUP BY U1.user_id, U2.user_id " +
                "HAVING COUNT(*) > 0 " +
                "ORDER BY likes_intersection DESC " +
                "LIMIT 10;";
        String userLikesSql = "Select film_id from likes where user_id = ?";
        SqlRowSet userLikesRs = jdbcTemplate.queryForRowSet(sql);
        while (userLikesRs.next()) {
            User user = User.builder().id(userLikesRs.getInt("user_id"))
                    .email(userLikesRs.getString("email"))
                    .login(userLikesRs.getString("login"))
                    .name(userLikesRs.getString("name"))
                    .birthday(userLikesRs.getDate("birthday").toLocalDate()).build();
            int filmId = userLikesRs.getInt("film_id");
            List<Integer> likes = usersWithLikes.getOrDefault(user, new ArrayList<>());
            likes.add(filmId);
            usersWithLikes.put(user, likes);
        }
        User targetUser = getUser(userId);
        if (!usersWithLikes.containsKey(targetUser)) {
            List<Integer> targetUserLikes = jdbcTemplate.queryForList(userLikesSql, Integer.class, userId);
            usersWithLikes.put(targetUser, targetUserLikes);
        }

        List<Integer> recommendations = new ArrayList<>();
        List<User> similarusers = new ArrayList<>(usersWithLikes.keySet());
        for (User similaruser : similarusers) {
            List<Integer> userLikes = usersWithLikes.get(similaruser);
            userLikes.removeAll(usersWithLikes.get(targetUser));
            recommendations.addAll(userLikes);
        }

        List<Integer> recommendedFilmsIds = new ArrayList<>();
        for (int filmId : recommendations) {
            if (!usersWithLikes.get(targetUser).contains(filmId)) {
                recommendedFilmsIds.add(filmId);
            }
        }
        return recommendedFilmsIds;
    }
}