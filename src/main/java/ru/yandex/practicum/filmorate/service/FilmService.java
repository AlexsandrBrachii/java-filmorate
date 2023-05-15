package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dao.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.dao.eventEnum.EventType;

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
            throw new NotFoundException("film с id=" + id + " не найден");
        }
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorageDb.findAllFilms();
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        Mpa mpa = filmStorageDb.checkMpa(film);
        List<Genre> genres = filmStorageDb.checkGenre(film);
        Film film1 = filmStorageDb.addFilm(film);
        film1.setMpa(mpa);
        film1.setGenres(genres);
        filmStorageDb.insertFilmGenres(film1);

        if (film.getDirectors() != null) {
            Collection<Director> directors = film.getDirectors();
            for (Director director : directors) {
                int idDir = director.getId();
                filmStorageDb.addDirectorsByFilmId(film.getId(), idDir);
            }
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

        filmStorageDb.deleteDirectorsByFilmId(enteredFilm.getId());
        if (enteredFilm.getDirectors() != null) {
            Collection<Director> directors = enteredFilm.getDirectors();
            for (Director director : directors) {
                int idDir = director.getId();
                filmStorageDb.addDirectorsByFilmId(enteredFilm.getId(), idDir);
            }
        }
        return filmForResult;
    }

    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        Director director = directorRepository.findById(directorId).orElseThrow(() -> new NotFoundException("404"));

        List<Film> films = new ArrayList<>(filmStorageDb.findAllFilms());

        Comparator<Film> comparator = null;
        if (sortBy.equals("year")) {
            comparator = Comparator.comparing(Film::getReleaseDate);
        } else if (sortBy.equals("likes")) {
            comparator = Comparator.comparingInt(Film::getRate).reversed();
        } else {
            String message = String.format("Сортировка по типу %s отсутствует", sortBy);
            throw new IllegalStateException(message);
        }

        return films.stream()
            .sorted(comparator)
            .filter(film -> !film.getDirectors().isEmpty())
            .collect(Collectors.toList());
    }


    public Collection<Film> getSearchFilms(String query, String by) {
        List<String> searchFields = Arrays.asList(by.split(","));

        List<Film> films = new ArrayList<>(this.getAllFilms());
        List<Film> result = new ArrayList<>();

        final boolean isDirectorProvided = (searchFields.contains("director"));
        final boolean isTitleProvided = searchFields.contains("title");

        for (Film film : films) {
            boolean isFilmAdded = false;

            if (isTitleProvided && film.getName().toLowerCase().contains(query.toLowerCase())) {
                result.add(film);
                isFilmAdded = true;
            }

            if (isDirectorProvided && !isFilmAdded && film.getDirectors().stream()
                .anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase()))) {
                result.add(film);
            }
        }
        result.sort(Comparator.comparing(Film::getId).reversed());
        return result;
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

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorageDb.getCommonFilms(userId, friendId);
    }

    public String deleteFilm(Integer id) {
        return filmStorageDb.deleteFilm(id);
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
        return filmStorageDb.getFilm(userStorageDb.getRecommendations(id, targetUser));
    }
}
