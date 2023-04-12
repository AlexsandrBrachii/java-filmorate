package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final UserStorageDb userStorage;
    private final FilmStorageDb filmStorage;

    @Test
    @DirtiesContext
    void getFilm_withNormalBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilm(filmTest.getId()));

        assertTrue(filmOptional.isPresent());
        Film film = filmOptional.get();
        assertEquals(1, film.getId());
    }

    @Test
    @DirtiesContext
    void getAllFilms_withNormalBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest);
        Film filmTest1 = Film.builder()
                .id(2)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest1);

        Collection<Film> films = filmStorage.getAllFilms();

        assertEquals(2, films.size());
    }

    @Test
    @DirtiesContext
    void addFilm_withNormalBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilm(filmTest.getId()));

        assertTrue(filmOptional.isPresent());
        Film film = filmOptional.get();
        assertEquals(1, film.getId());
    }

    @Test
    @DirtiesContext
    void updateFilm_withNormalBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest);
        Film filmTest1 = Film.builder()
                .id(1)
                .name("name")
                .description("new description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.updateFilm(filmTest1);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilm(filmTest.getId()));

        assertTrue(filmOptional.isPresent());
        Film film = filmOptional.get();
        assertEquals("new description", film.getDescription());
    }

    @Test
    @DirtiesContext
    void makeLike_withNormalBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest);

        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        filmStorage.makeLike(filmTest.getId(), userTest.getId());
        List<Integer> likes = filmStorage.getFilmLikes(filmTest.getId());

        assertEquals(1, likes.size());
        assertEquals(userTest.getId(), likes.get(0));
    }

    @Test
    @DirtiesContext
    void deleteLike() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new MPA(4, "R")).build();
        filmStorage.addFilm(filmTest);

        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        filmStorage.makeLike(filmTest.getId(), userTest.getId());
        List<Integer> likes = filmStorage.getFilmLikes(filmTest.getId());

        assertEquals(1, likes.size());
        assertEquals(userTest.getId(), likes.get(0));

        filmStorage.deleteLike(filmTest.getId(), userTest.getId());

        List<Integer> likes1 = filmStorage.getFilmLikes(filmTest.getId());

        assertTrue(likes1.isEmpty());
    }
}