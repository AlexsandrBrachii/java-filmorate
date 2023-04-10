package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void makeLike(int idFilm, int idUser);

    void deleteLike(int idFilm, int idUser);

    Collection<Film> getPopularFilms(int count);
}