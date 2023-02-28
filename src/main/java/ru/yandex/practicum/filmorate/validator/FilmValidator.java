package ru.yandex.practicum.filmorate.validator;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    public static void validateFilm(Film film) {
        try {
            if (film.getName().isBlank()) {
                log.info("Название фильма не может быть пустым.");
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Название фильма не может быть пустым.");
            }
            if (film.getDescription().length() > 200) {
                log.info("Длина описания не может превышать больше 200 символов.");
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Длина описания не может превышать больше 200 символов.");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.info("Дата релиза не может быть раньше 28 Декабря 1895.");
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Дата релиза не может быть раньше 28 Декабря 1895.");
            }
            if (film.getDuration() <= 0) {
                log.info("Продолжительность фильма должна быть положительной.");
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Продолжительность фильма должна быть положительной.");
            }
        } catch (NullPointerException ex) {
            log.info("Поля фильма не могут быть null.");
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "Поля фильма не могут быть null.");
        }
    }
}
