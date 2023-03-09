package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;



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
            throw new NotFoundException("film с id=" + id + " не найден.");
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
            throw new NotFoundException("film " + film.getName() + " не найден.");
        }
        films.put(film.getId(), film);
        log.info("film " + film.getName() + " обновлён.");
        return film;
    }
}