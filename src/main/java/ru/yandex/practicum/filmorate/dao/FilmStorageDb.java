package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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

    Mpa checkMpa(Film film);

    List<Genre> getGenres(int idFilm);

    List<Genre> checkGenre(Film film);

    void insertFilmGenres(Film film);

    void deleteFilmGenres(Film film);

    void addDirectorsByFilmId(Collection<Director> directors, Integer filmId);

    Collection<Director> findDirectorsByFilmId(Integer filmId);

    List<Film> findByDirectorIdAndSortByRelateDate(Integer directorId);

    List<Film> findByDirectorIdAndSortByLikes(Integer directorId);

    Collection<Film> getCommonFilms(int userId, int friendId);

    Collection<Film> getSearchFilms(String query, List<String> by);

    Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year);
}
