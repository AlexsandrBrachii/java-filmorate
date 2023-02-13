package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Integer identifier = 1;
    private HashMap<Integer, Film> films = new HashMap<>();


    @GetMapping
    private Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    private Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        film.setId(identifier);
        films.put(identifier, film);
        identifier++;
        log.info("film " + film.getName() + " добавлен.");
        return film;
    }

    @PutMapping
    private Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("film " + film.getName() + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "film " + film.getName() + " не найден.");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("film " + film.getName() + " обновлён.");
        return film;
    }
}