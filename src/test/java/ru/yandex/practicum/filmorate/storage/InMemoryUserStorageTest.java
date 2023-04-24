package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InMemoryUserStorageTest {

    @Autowired
    private InMemoryUserStorage storage;

    @Test
    void getUser_WithNormalBehavior() {
        User user = User.builder()
                .email("awb@mail.ru")
                .login("awb")
                .name("name")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        storage.createUser(user);

        assertEquals(user, storage.getUser(user.getId()));
    }

    @Test
    void getUser_WithWrongId() {
        int id = 999;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> storage.getUser(id));

        assertEquals("user с id=" + id + " не найден.", exception.getMessage());
    }

    @Test
    void updateUser_WithoutId() {
        User user = User.builder()
                .email("awb@mail.ru")
                .login("awb")
                .name("name")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> storage.updateUser(user));

        assertEquals("user " + user.getName() + " не найден.", exception.getMessage());
    }
}