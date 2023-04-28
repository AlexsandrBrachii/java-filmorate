package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmStorageDb {

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    String deleteFilm(int filmId);

    void makeLike(int idFilm, int idUser);

    void deleteLike(int idFilm, int idUser);

    Collection<Film> getPopularFilms(int count);

    List<Integer> getFilmLikes(Integer id);

    MPA checkMpa(Film film);

    List<Genre> getGenres(int idFilm);

    List<Genre> checkGenre(Film film);

    void insertFilmGenres(Film film);

    void deleteFilmGenres(Film film);

    Collection<Film> getCommonFilms(int userId, int friendId);
}
