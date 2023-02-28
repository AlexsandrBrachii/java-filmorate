package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@SpringBootTest
public class UserValidatorTest {


    @Test
    public void validateUser_WithEmptyEmail() {
        User user = User.builder()
                .id(1)
                .email("")
                .login("awb")
                .name("alex")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }

    @Test
    public void validateUser_WithInvalidEmail() {
        User user = User.builder()
                .id(1)
                .email("awb.mail.ru")
                .login("awb")
                .name("alex")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }

    @Test
    public void validateUser_WithEmptyLogin() {
        User user = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login(" ")
                .name("alex")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
    }

    @Test
    public void validateUser_WithInvalidLogin() {
        User user = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb b")
                .name("alex")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
    }

    @Test
    public void validateUser_WithEmptyName() {
        User user = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("")
                .birthday(LocalDate.parse("1996-09-08"))
                .build();

        validateUser(user);

        assertEquals(user.getLogin(), user.getName(), "Имя сохраняется не правильно.");
    }

    @Test
    public void validateUser_WithInvalidBirthday() {
        User user = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("")
                .birthday(LocalDate.parse("2100-09-08"))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }
}
