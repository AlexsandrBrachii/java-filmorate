package ru.yandex.practicum.filmorate.validator;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {

    @ResponseBody
    public static void validateUser(User user) {
        try {
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                log.info("Электронная почта не может быть пустой и должна содержать символ @.");
                throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
            }
            if (user.getLogin() == null || user.getLogin().contains(" ")) {
                log.info("Логин не может быть пустым и содержать пробелы.");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
            }
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.info("Дата рождения не может быть в будущем.");
                throw new ValidationException("Дата рождения не может быть в будущем.");
            }
        } catch (NullPointerException ex) {
            log.info("Поля пользователя не могут быть null.");
            throw new ValidationException("Поля пользователя не могут быть null.");
        }
    }
}