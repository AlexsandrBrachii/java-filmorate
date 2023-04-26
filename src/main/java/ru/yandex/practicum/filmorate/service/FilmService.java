package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.FilmStorageDb;
import ru.yandex.practicum.filmorate.dao.UserStorageDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorageDb filmStorageDb;
    private final UserStorageDb userStorageDb;
    private final DirectorRepository directorRepository;

    public Film getFilm(int id) {
        Film film = filmStorageDb.getFilm(id);
        if (film == null) {
            throw new NotFoundException("film с id=" + id + "не найден");
        }
        List<Genre> genres = filmStorageDb.getGenres(id);
        film.setGenres(genres);

        collectDirectors(film);

        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorageDb.getAllFilms().stream()
                .peek(this::collectDirectors)
                .collect(Collectors.toList());
    }

    private void collectDirectors(Film film) {
        Collection<Director> directors = filmStorageDb.findDirectorsByFilmId(film.getId());
        if (!directors.isEmpty()) {
            film.getDirectors().clear();
            film.getDirectors().addAll(directors);
        }
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        Mpa mpa = filmStorageDb.checkMpa(film);
        List<Genre> genres = filmStorageDb.checkGenre(film);
        Film film1 = filmStorageDb.addFilm(film);
        film1.setMpa(mpa);
        film1.setGenres(genres);
        filmStorageDb.insertFilmGenres(film1);

        if (!film.getDirectors().isEmpty()) {
            filmDirectorExecuteProcessing(film, film1);
        }
        return film1;
    }

    public Film updateFilm(Film enteredFilm) {
        validateFilm(enteredFilm);
        getFilm(enteredFilm.getId());
        Mpa mpa = filmStorageDb.checkMpa(enteredFilm);
        List<Genre> genres = filmStorageDb.checkGenre(enteredFilm);
        Film filmForResult = filmStorageDb.updateFilm(enteredFilm);
        filmForResult.setMpa(mpa);
        filmForResult.setGenres(genres);
        filmStorageDb.deleteFilmGenres(filmForResult);
        filmStorageDb.insertFilmGenres(filmForResult);

        filmDirectorExecuteProcessing(enteredFilm, filmForResult);

        return filmForResult;
    }

    private void filmDirectorExecuteProcessing(Film film, Film filmForResult) {
        final Set<Director> collectedDirectors =
                film.getDirectors().stream()
                        .map(director -> directorRepository.findById(director.getId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());

        filmStorageDb.addDirectorsByFilmId(collectedDirectors, film.getId());

        filmForResult.getDirectors().clear();
        filmForResult.getDirectors().addAll(collectedDirectors);
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

    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {

        directorRepository.findById(directorId).orElseThrow(() -> new NotFoundException("404"));

        if (sortBy.equals("year")) {
            return filmStorageDb.findByDirectorIdAndSortByRelateDate(directorId)
                    .stream()
                    .peek(this::collectDirectors)
                    .peek(film -> {
                        List<Genre> genres = filmStorageDb.getGenres(film.getId());
                        film.setGenres(genres);
                    })
                    .collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            return filmStorageDb.findByDirectorIdAndSortByLikes(directorId)
                    .stream()
                    .peek(this::collectDirectors)
                    .peek(film -> {
                        List<Genre> genres = filmStorageDb.getGenres(film.getId());
                        film.setGenres(genres);
                    })
                    .collect(Collectors.toList());
        }

        String message = String.format("Сортировка по типу %s отсутствует", sortBy);
        throw new IllegalStateException(message);
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorageDb.getCommonFilms(userId, friendId);
    }

    public String deleteFilm(Integer id) {
        return filmStorageDb.deleteFilm(id);
    }

    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        return filmStorageDb.getPopularFilmsByGenreAndYear(count, genreId, year);
    }
}
