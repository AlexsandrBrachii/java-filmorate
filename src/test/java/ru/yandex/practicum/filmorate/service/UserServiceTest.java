package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.addFriend(userId, filmId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("id пользователей не может быть отрицательным.", exception.getReason());
    }

    @Test
    void addFriend_WithWrongUserId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongUserId = 55;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.addFriend(wrongUserId, friend.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("user с id=" + wrongUserId + " не найден.", exception.getReason());
    }

    @Test
    void addFriend_WithWrongFriendId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongFriendId = 55;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.addFriend(user.getId(), wrongFriendId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("friend с id=" + wrongFriendId + " не найден.", exception.getReason());
    }

    @Test
    void deleteFriend_WithNegativeId() {
        int userId = 0;
        int filmId = -1;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deleteFriend(userId, filmId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("id не может быть отрицательным.", exception.getReason());
    }

    @Test
    void deleteFriend_WithWrongUserId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongUserId = 55;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deleteFriend(wrongUserId, friend.getId()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("user с id=" + wrongUserId + " не найден.", exception.getReason());
    }

    @Test
    void deleteFriend_WithWrongFriendId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        User friend = new User("awb@mail.ru", "awb1", LocalDate.parse("1996-09-08"));
        storage.createUser(friend);
        int wrongFriendId = 55;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deleteFriend(user.getId(), wrongFriendId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("friend с id=" + wrongFriendId + " не найден.", exception.getReason());
    }

    @Test
    void getFriends_WithNegativeId() {
        int userId = -1;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.getFriends(userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("id не может быть отрицательным.", exception.getReason());
    }

    @Test
    void getFriends_WithWrongId() {
        User user = new User("awb@mal.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        int userId = 999;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.getFriends(userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("user с id=" + userId + " не найден.", exception.getReason());
    }

    @Test
    void haveCommonFriends_WithNegativeId() {
        int userId = 0;
        int filmId = -1;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.haveCommonFriends(userId, filmId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("id не может быть отрицательным.", exception.getReason());
    }
}
