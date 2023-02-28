package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }



    public void makeLike(int idFilm, int idUser) {
        if (idFilm < 1 || idUser < 1) {
            log.info("id не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id не может быть отрицательным.");
        }
        Collection<Film> films = filmStorage.getAllFilms();

        Optional<Film> optionalFilm = films.stream()
                .filter(film -> film.getId().equals(idFilm))
                .findFirst();

        if (optionalFilm.isPresent()) {
            Film film = optionalFilm.get();
            film.getLikes().add((long) idUser);
            log.info("User с id=" + idUser + " поставил лайк film с id=" + idFilm);
        } else {
            log.info("film с id=" + idFilm + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "film с id=" + idFilm + " не найден.");
        }
    }

    public void deleteLike(int idFilm, int idUser) {
        if (idFilm < 1 || idUser < 1) {
            log.info("id не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id не может быть отрицательным.");
        }
        Collection<Film> films = filmStorage.getAllFilms();

        if (films.isEmpty()) {
            log.info("Список фильмов пустой.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Список фильмов пустой.");
        }

        Optional<Film> optionalFilm = films.stream()
                .filter(film -> film.getId().equals(idFilm))
                .findFirst();

        if (optionalFilm.isPresent()) {
            Film film = optionalFilm.get();
            film.getLikes().remove((long) idUser);
            log.info("User с id=" + idUser + " удалил свой лайк film с id=" + idFilm);
        } else {
            log.info("film с id=" + idFilm + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "film с id=" + idFilm + " не найден.");
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count < 1) {
            log.info("count не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "count не может быть отрицательным.");
        }
        Collection<Film> films = filmStorage.getAllFilms();

        if (!films.isEmpty()) {
            Collection<Film> popularFilms = films.stream()
                    .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                    .limit(count)
                    .collect(Collectors.toList());

            return popularFilms;
        } else {
            log.info("Список фильмов пустой.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Список фильмов пустой.");
        }
    }
}
