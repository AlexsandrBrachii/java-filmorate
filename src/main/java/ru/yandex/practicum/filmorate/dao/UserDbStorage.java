package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
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
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        User user = null;
        if (userRows.next()) {
            user = User.builder().id(id)
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate()).build();
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
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate()).build();
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
        Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
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
        User targetUser;
        try {
            targetUser = getUser(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("пользователя с таким id не существует");
        }
        String sql = "SELECT u2.*, COUNT(l2.film_id) as likes_intersection " +
                "FROM likes l1 " +
                "JOIN likes l2 ON l1.film_id = l2.film_id AND l1.user_id != l2.user_id " +
                "JOIN users u2 on u2.user_id = l2.user_id " +
                "WHERE l1.user_id = ? " +
                "GROUP BY l1.user_id, l2.user_id, u2.user_id " +
                "ORDER BY likes_intersection DESC " +
                "LIMIT 10";
        String userLikesSql = "Select film_id from likes where user_id = ?";
        SqlRowSet userLikesRs = jdbcTemplate.queryForRowSet(sql, userId);
        while (userLikesRs.next()) {
            User user = User.builder().id(userLikesRs.getInt("user_id"))
                    .email(userLikesRs.getString("email"))
                    .login(userLikesRs.getString("login"))
                    .name(userLikesRs.getString("name"))
                    .birthday(Objects.requireNonNull(userLikesRs.getDate("birthday")).toLocalDate()).build();
            usersWithLikes.put(user, new ArrayList<>());
        }
        usersWithLikes.keySet().forEach(user -> usersWithLikes.get(user).addAll(jdbcTemplate.queryForList(userLikesSql,
                Integer.class, user.getId())));
        if (!usersWithLikes.containsKey(targetUser)) {
            List<Integer> targetUserLikes = jdbcTemplate.queryForList(userLikesSql, Integer.class, userId);
            usersWithLikes.put(targetUser, targetUserLikes);
        }

        List<Integer> recommendations = new ArrayList<>();
        List<User> similarUsers = new ArrayList<>(usersWithLikes.keySet());
        for (User similaruser : similarUsers) {
            if (similaruser.equals(targetUser)) {
                continue;
            }
            List<Integer> similarUserLikes = usersWithLikes.get(similaruser);
            similarUserLikes.removeAll(usersWithLikes.get(targetUser));
            recommendations.addAll(similarUserLikes);
        }

        List<Integer> recommendedFilmsIds = new ArrayList<>();
        for (int filmId : recommendations) {
            if (!usersWithLikes.get(targetUser).contains(filmId)) {
                recommendedFilmsIds.add(filmId);
                log.info("Пользователю рекомендован фильм с id " + filmId);
            }
        }
        return recommendedFilmsIds;
    }
}