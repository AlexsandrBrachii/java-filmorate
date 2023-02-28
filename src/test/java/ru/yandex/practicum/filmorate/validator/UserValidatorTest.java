package ru.yandex.practicum.filmorate.validator;

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
        User user = new User( "", "awb", LocalDate.parse("1996-09-08"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }

    @Test
    public void validateUser_WithInvalidEmail() {
        User user = new User( "", "awb.mail.ru", LocalDate.parse("1996-09-08"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }

    @Test
    public void validateUser_WithEmptyLogin() {
        User user = new User("awb@mail.ru", " ", LocalDate.parse("1996-09-08"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
    }

    @Test
    public void validateUser_WithInvalidLogin() {
        User user = new User("awb@mail.ru", "awb b", LocalDate.parse("1996-09-08"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
    }

    @Test
    public void validateUser_WithEmptyName() {
        User user = new User("awb@mail.ru", "awb", LocalDate.parse("1996-09-08"));

        validateUser(user);

        assertEquals(user.getLogin(), user.getName(), "Имя сохраняется не правильно.");
    }

    @Test
    public void validateUser_WithInvalidBirthday() {
        User user = new User("awb@mail.ru", "awb", LocalDate.parse("2100-09-08"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateUser(user);
        });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }
}
