package ru.yandex.practicum.filmorate.validator;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    @ResponseBody
    public static void validateFilm(Film film) {
        try {
            if (film.getName().isBlank()) {
                log.info("Название фильма не может быть пустым.");
                throw new ValidationException("Название фильма не может быть пустым.");
            }
            if (film.getDescription().length() > 200) {
                log.info("Длина описания не может превышать больше 200 символов.");
                throw new ValidationException("Длина описания не может превышать больше 200 символов.");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.info("Дата релиза не может быть раньше 28 Декабря 1895.");
                throw new ValidationException("Дата релиза не может быть раньше 28 Декабря 1895.");
            }
            if (film.getDuration() <= 0) {
                log.info("Продолжительность фильма должна быть положительной.");
                throw new ValidationException("Продолжительность фильма должна быть положительной.");
            }
        } catch (NullPointerException ex) {
            log.info("Поля фильма не могут быть null.");
            throw new ValidationException("Поля фильма не могут быть null.");
        }
    }
}