package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.makeLike(filmId, userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("id не может быть отрицательным.", exception.getReason());
    }

    @Test
    void makeLike_WithWrongIdFilm() {
        Film film = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        storage.addFilm(film);
        int filmId = 999;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.makeLike(filmId, 3));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("film с id=" + filmId + " не найден.", exception.getReason());
    }

    @Test
    void deleteLike_WithNegativeId() {
        int filmId = -1;
        int userId = 0;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deleteLike(filmId, userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("id не может быть отрицательным.", exception.getReason());
    }

    @Test
    void deleteLike_WithWrongIdFilm() {
        Film film = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        storage.addFilm(film);
        int filmId = 999;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deleteLike(filmId, 3));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("film с id=" + filmId + " не найден.", exception.getReason());
    }

    @Test
    void getPopularFilms_WithNegativeId() {
        int count = -1;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.getPopularFilms(count));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("count не может быть отрицательным.", exception.getReason());
    }
}
