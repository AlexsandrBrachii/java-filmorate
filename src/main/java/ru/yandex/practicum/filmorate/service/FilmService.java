package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

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
        filmDbStorage.makeLike(idFilm, idUser);
    }

    public void deleteLike(int idFilm, int idUser) {
        filmDbStorage.deleteLike(idFilm, idUser);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmDbStorage.getPopularFilms(count);
    }
}