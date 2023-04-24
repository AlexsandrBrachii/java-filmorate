package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InMemoryFilmStorageTest {

    @Autowired
    private InMemoryFilmStorage storage;

    @Test
    void getFilm_WithNormalBehavior() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90).build();

        storage.addFilm(film);

        assertEquals(film, storage.getFilm(film.getId()));
    }

    @Test
    void getFilm_WithWrongId() {
        int id = 999;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> storage.getFilm(id));

        assertEquals("film с id=" + id + " не найден.", exception.getMessage());
    }

    @Test
    void updateFilm_WithoutId() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90).build();

        assertThrows(NotFoundException.class, () -> storage.updateFilm(film));
    }
}