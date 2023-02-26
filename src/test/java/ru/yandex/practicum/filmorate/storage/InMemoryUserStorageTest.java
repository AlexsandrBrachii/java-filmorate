package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InMemoryUserStorageTest {

    @Autowired
    private InMemoryUserStorage storage;


    @Test
    void getUser_WithNormalBehavior() {
        User user = new User( "awb@mail.ru", "awb", LocalDate.parse("1996-09-08"));
        storage.createUser(user);
        assertEquals(user, storage.getUser(user.getId()));
    }

    @Test
    void getUser_WithWrongId() {
        int id = 999;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> storage.getUser(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("user с id=" + id + " не найден.", exception.getReason());
    }

    @Test
    void getAllUsers_WithEmptyList() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> storage.getAllUsers());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Список с пользователями пустой.", exception.getReason());
    }

    @Test
    void updateUser_WithoutId() {
        User user = new User( "awb@mail.ru", "awb", LocalDate.parse("1996-09-08"));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> storage.updateUser(user));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("user " + user.getName() + " не найден.", exception.getReason());
    }
}
