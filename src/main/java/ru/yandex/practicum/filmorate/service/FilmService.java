package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dao.event_enum.EventOperation;
import ru.yandex.practicum.filmorate.dao.event_enum.EventType;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorageDb;
    private final UserStorage userStorageDb;
    private final DirectorStorage directorRepository;
    private final EventService eventService;

    public Film getFilm(int id) {
        Film film;
        try {
            film = filmStorageDb.getFilm(List.of(id)).get(0);
        } catch (IndexOutOfBoundsException e) {
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
        eventService.createEvent(idUser, EventType.LIKE, EventOperation.ADD, idFilm);
    }

    public void deleteLike(int idFilm, int idUser) {
        User user = userStorageDb.getUser(idUser);
        if (user == null) {
            throw new NotFoundException("User с id " + idUser + " не найден.");
        }
        filmStorageDb.deleteLike(idFilm, idUser);
        eventService.createEvent(idUser, EventType.LIKE, EventOperation.REMOVE, idFilm);
    }

    public Collection<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        if (count < 1) {
            log.info("count не может быть отрицательным.");
            throw new NotFoundException("count не может быть отрицательным.");
        }
        if (genreId != null || year != null) {
            return filmStorageDb.getPopularFilmsByGenreAndYear(count, genreId, year);
        }
        return new HashSet<>(filmStorageDb.getPopularFilms(count));
    }

    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {

        directorRepository.findById(directorId).orElseThrow(() -> new NotFoundException("404"));

        final List<Film> collectedFilms = filmStorageDb.findByDirectorIdAndSortBy(directorId)
            .stream()
            .peek(this::collectDirectors)
            .peek(film -> {
                List<Genre> genres = filmStorageDb.getGenres(film.getId());
                film.setGenres(genres);
            })
            .collect(Collectors.toList());

        Comparator<Film> comparator = null;

        if (sortBy.equals("year")) {
            comparator = Comparator.comparing(Film::getReleaseDate);
        } else if (sortBy.equals("likes")) {
            comparator = Comparator.comparingInt(Film::getRate).reversed();
        } else {
            String message = String.format("Сортировка по типу %s отсутствует", sortBy);
            throw new IllegalStateException(message);
        }
        return collectedFilms.stream().sorted(comparator).collect(Collectors.toList());


    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorageDb.getCommonFilms(userId, friendId);
    }

    public String deleteFilm(Integer id) {
        return filmStorageDb.deleteFilm(id);
    }

    public Collection<Film> getSearchFilms(String query, String by) {
        List<String> searchFields = Arrays.asList(by.split(","));
        Collection<Film> result = new HashSet<>();
        if (searchFields.contains("director")) {
            result.addAll(filmStorageDb.getSearchFilmsByDirector(query));
        }
        if (searchFields.contains("title")) {
            result.addAll(filmStorageDb.getSearchFilmsByTitle(query));
        }

        return result.stream()
                .peek(this::collectDirectors)
                .collect(Collectors.toList());
    }

    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        return filmStorageDb.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public List<Film> getRecommendations(int id) {
        User targetUser;
        try {
            targetUser = userStorageDb.getUser(id);
        } catch (RuntimeException e) {
            throw new NotFoundException("пользователя с таким id не существует");
        }
        return filmStorageDb.getFilm(userStorageDb.getRecommendations(id,targetUser));
    }
}
