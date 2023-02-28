package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {


    private Integer identifier = 1;

    private HashMap<Integer, Film> films = new HashMap<>();


    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.info("film с id=" + id + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "film с id=" + id + " не найден.");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        if (films.isEmpty()) {
            log.info("Список с фильмами пустой.");
        }
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(identifier);
        films.put(identifier, film);
        identifier++;
        log.info("film " + film.getName() + " добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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