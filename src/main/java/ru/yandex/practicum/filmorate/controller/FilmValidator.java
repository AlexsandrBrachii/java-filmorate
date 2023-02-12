package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {

    private static final Logger log = LoggerFactory.getLogger(FilmValidator.class);

    protected static void validateFilm(Film film) {
        if (film.getName().trim().isEmpty()) {
            log.error("Название фильма не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Длина описания не может превышать больше 200 символов.");
            throw new ValidationException("Длина описания не может превышать больше 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не может быть раньше 28 Декабря 1895.");
            throw new ValidationException("Дата релиза не может быть раньше 28 Декабря 1895.");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
