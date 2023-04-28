package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorageDb;
import ru.yandex.practicum.filmorate.dao.UserStorageDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorageDb filmStorageDb;
    private final UserStorageDb userStorageDb;

    public Film getFilm(int id) {
        Film film = filmStorageDb.getFilm(id);
        if (film == null) {
            throw new NotFoundException("film с id=" + id + "не найден");
        }
        List<Genre> genres = filmStorageDb.getGenres(id);
        film.setGenres(genres);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorageDb.getAllFilms();
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        MPA mpa = filmStorageDb.checkMpa(film);
        List<Genre> genres = filmStorageDb.checkGenre(film);
        Film film1 = filmStorageDb.addFilm(film);
        film1.setMpa(mpa);
        film1.setGenres(genres);
        filmStorageDb.insertFilmGenres(film1);
        return film;
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        getFilm(film.getId());
        MPA mpa = filmStorageDb.checkMpa(film);
        List<Genre> genres = filmStorageDb.checkGenre(film);
        Film film1 = filmStorageDb.updateFilm(film);
        film1.setMpa(mpa);
        film1.setGenres(genres);
        filmStorageDb.deleteFilmGenres(film1);
        filmStorageDb.insertFilmGenres(film1);
        return film1;
    }

    public void makeLike(int idFilm, int idUser) {
        filmStorageDb.makeLike(idFilm, idUser);
    }

    public void deleteLike(int idFilm, int idUser) {
        User user = userStorageDb.getUser(idUser);
        if (user == null) {
            throw new NotFoundException("User с id " + idUser + " не найден.");
        }
        filmStorageDb.deleteLike(idFilm, idUser);
    }

    public Set<Film> getPopularFilms(int count) {
        if (count < 1) {
            log.info("count не может быть отрицательным.");
            throw new NotFoundException("count не может быть отрицательным.");
        }
        return new HashSet<>(filmStorageDb.getPopularFilms(count));
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorageDb.getCommonFilms(userId, friendId);
    }

    public String deleteFilm(Integer id) {
        return filmStorageDb.deleteFilm(id);
    }
}