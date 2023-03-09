package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Autowired
    private InMemoryUserStorage storage;



    @Test
    void addFriend_WithNegativeId() {
        int userId = 0;
        int filmId = -1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.addFriend(userId, filmId));

        assertEquals("user с id=" + userId + " не найден.", exception.getMessage());
    }

    @Test
    void addFriend_WithWrongUserId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongUserId = 55;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.addFriend(wrongUserId, friend.getId()));

        assertEquals("user с id=" + wrongUserId + " не найден.", exception.getMessage());
    }

    @Test
    void addFriend_WithWrongFriendId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongFriendId = 55;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.addFriend(user.getId(), wrongFriendId));

        assertEquals("user с id=" + wrongFriendId + " не найден.", exception.getMessage());
    }

    @Test
    void deleteFriend_WithNegativeId() {
        int userId = 0;
        int filmId = -1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deleteFriend(userId, filmId));

        assertEquals("user с id=" + userId + " не найден.", exception.getMessage());
    }

    @Test
    void deleteFriend_WithWrongUserId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongUserId = 55;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deleteFriend(wrongUserId, friend.getId()));

        assertEquals("user с id=" + wrongUserId + " не найден.", exception.getMessage());
    }

    @Test
    void deleteFriend_WithWrongFriendId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongFriendId = 55;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deleteFriend(user.getId(), wrongFriendId));

        assertEquals("user с id=" + wrongFriendId + " не найден.", exception.getMessage());
    }

    @Test
    void getFriends_WithNegativeId() {
        int userId = -1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getFriends(userId));

        assertEquals("user с id=" + userId + " не найден.", exception.getMessage());
    }

    @Test
    void getFriends_WithWrongId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        int userId = 999;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getFriends(userId));

        assertEquals("user с id=" + userId + " не найден.", exception.getMessage());
    }

    @Test
    void haveCommonFriends_WithNegativeId() {
        int userId = 0;
        int filmId = -1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.haveCommonFriends(userId, filmId));

        assertEquals("user с id=" + userId + " не найден.", exception.getMessage());
    }
}
