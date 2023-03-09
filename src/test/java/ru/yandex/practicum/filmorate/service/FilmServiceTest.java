package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmServiceTest {

    @Autowired
    private FilmService service;

    @Autowired
    private InMemoryFilmStorage storage;




    @Test
    void makeLike_WithNegativeId() {
        int userId = 0;
        int filmId = -1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.makeLike(filmId, userId));

        assertEquals("film с id=" + filmId + " не найден.", exception.getMessage());
    }

    @Test
    void makeLike_WithWrongIdFilm() {
        Film film = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        storage.addFilm(film);
        int filmId = 999;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.makeLike(filmId, 3));

        assertEquals("film с id=" + filmId + " не найден.", exception.getMessage());
    }

    @Test
    void deleteLike_WithNegativeId() {
        int filmId = -1;
        int userId = 0;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deleteLike(filmId, userId));

        assertEquals("film с id=" + filmId + " не найден.", exception.getMessage());
    }

    @Test
    void deleteLike_WithWrongIdFilm() {
        Film film = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        storage.addFilm(film);
        int filmId = 999;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deleteLike(filmId, 3));

        assertEquals("film с id=" + filmId + " не найден.", exception.getMessage());
    }

    @Test
    void getPopularFilms_WithNegativeId() {
        int count = -1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getPopularFilms(count));

        assertEquals("count не может быть отрицательным.", exception.getMessage());
    }
}
