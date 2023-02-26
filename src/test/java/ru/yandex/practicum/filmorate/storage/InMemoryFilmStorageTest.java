package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InMemoryFilmStorageTest {

    @Autowired
    private InMemoryFilmStorage storage;


    @Test
    void getFilm_WithNormalBehavior() {
        Film film = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        storage.addFilm(film);
        assertEquals(film, storage.getFilm(film.getId()));
    }

    @Test
    void getFilm_WithWrongId() {
        int id = 999;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> storage.getFilm(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("film с id=" + id + " не найден.", exception.getReason());
    }

    @Test
    void getAllFilms_WithEmptyList() {
        Collection<Film> films = storage.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void getAllFilms_WithNormalBehavior() {
        Film film1 = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        Film film2 = new Film("Челюсти-2", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        storage.addFilm(film1);
        storage.addFilm(film2);
        Collection<Film> films = storage.getAllFilms();
        assertEquals(List.of(film1, film2), List.copyOf(films));
    }

    @Test
    void updateFilm_WithoutId() {
        Film film = new Film("Челюсти", "Про акулу",
                LocalDate.of(2000, 1, 1), 90);
        assertThrows(ResponseStatusException.class, () -> storage.updateFilm(film));
    }


}
