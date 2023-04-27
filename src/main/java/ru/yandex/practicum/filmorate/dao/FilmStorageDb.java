package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.model.Director;

public interface FilmStorageDb {

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void makeLike(int idFilm, int idUser);

    void deleteLike(int idFilm, int idUser);

    Collection<Film> getPopularFilms(int count);

    List<Integer> getFilmLikes(Integer id);

    Mpa checkMpa(Film film);

    List<Genre> getGenres(int idFilm);

    List<Genre> checkGenre(Film film);

    void insertFilmGenres(Film film);

    void deleteFilmGenres(Film film);

    void addDirectorsByFilmId(Collection<Director> directors, Integer filmId);

    Collection<Director> findDirectorsByFilmId(Integer filmId);

    List<Film> findByDirectorIdAndSortBy(String sql, Integer directorId);

    Collection<Film> getCommonFilms(int userId, int friendId);
}
