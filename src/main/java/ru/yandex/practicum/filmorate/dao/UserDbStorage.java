package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorageDb {

    private Integer identifier = 1;
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
        List<User> list = jdbcTemplate.query(sql, new Object[]{idUser},
                (resultSet, i) -> User.builder().id(resultSet.getInt("user_id"))
                        .email(resultSet.getString("email"))
                        .login(resultSet.getString("login"))
                        .name(resultSet.getString("name"))
                        .birthday(resultSet.getDate("birthday").toLocalDate())
                        .build());
        return list;
    }
}