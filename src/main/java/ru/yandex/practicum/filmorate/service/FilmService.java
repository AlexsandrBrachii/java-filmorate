package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmDbStorage filmDbStorage;

    public Collection<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        return filmDbStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        return filmDbStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.updateFilm(film);
    }

    public void makeLike(int idFilm, int idUser) {
        Film film = filmStorage.getFilm(idFilm);
        userStorage.getUser(idUser);
        film.getLikes().add((long) idUser);
        log.info("User с id=" + idUser + " поставил лайк film с id=" + idFilm);
    }

    public void deleteLike(int idFilm, int idUser) {
        Film film = filmStorage.getFilm(idFilm);
        userStorage.getUser(idUser);
        film.getLikes().remove((long) idUser);
        log.info("User с id=" + idUser + " удалил свой лайк film с id=" + idFilm);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count < 1) {
            log.info("count не может быть отрицательным.");
            throw new NotFoundException("count не может быть отрицательным.");
        }
        Collection<Film> films = filmStorage.getAllFilms();

        Collection<Film> popularFilms = films.stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());

        return popularFilms;
    }
}