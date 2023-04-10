package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        filmDbStorage.checkMpa(film);
        filmDbStorage.checkGenre(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmDbStorage.checkMpa(film);
        filmDbStorage.checkGenre(film);
        return filmStorage.updateFilm(film);
    }

    public void makeLike(int idFilm, int idUser) {
        filmStorage.makeLike(idFilm, idUser);
    }

    public void deleteLike(int idFilm, int idUser) {
        userStorage.getUser(idUser);
        filmStorage.deleteLike(idFilm, idUser);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count < 1) {
            log.info("count не может быть отрицательным.");
            throw new NotFoundException("count не может быть отрицательным.");
        }
        return filmStorage.getPopularFilms(count);
    }
}