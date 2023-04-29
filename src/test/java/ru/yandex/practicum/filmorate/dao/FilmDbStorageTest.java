package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final UserService userService;
    private final FilmService filmService;
    private final FilmStorageDb filmStorage;

    @Test
    @DirtiesContext
    void getFilmLikes_withNormanBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(4, "R")).build();
        filmService.addFilm(filmTest);

        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest);
        filmService.makeLike(filmTest.getId(), userTest.getId());

        List<Integer> likes = filmStorage.getFilmLikes(filmTest.getId());

        assertEquals(1, likes.size());
        assertEquals(userTest.getId(), likes.get(0));
    }

    @Test
    @DirtiesContext
    void checkMpa_withNormalBehavior() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(4, "R")).build();
        filmService.addFilm(filmTest);

        Mpa mpa = filmStorage.checkMpa(filmTest);

        assertEquals(4, mpa.getId());
        assertEquals("R", mpa.getName());
    }


    @Test
    @DirtiesContext
    void checkMpa_withWrongMpaId() {
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(9, "R")).build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmService.addFilm(filmTest);
        });

        assertEquals("Не найден MPA с id: 9", exception.getMessage());
    }

    @Test
    @DirtiesContext
    void checkGenre_withNormalBehavior() {
        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre(1, "Комедия");
        genres.add(genre);
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(4, "R"))
                .genres(genres).build();
        filmService.addFilm(filmTest);

        List<Genre> genreList = filmStorage.checkGenre(filmTest);

        assertEquals(1, genreList.size());
        Genre genre1 = genreList.get(0);
        assertEquals(1, genre1.getId());
        assertEquals("Комедия", genre1.getName());
    }

    @Test
    @DirtiesContext
    void checkGenre_withWrongGenreId() {
        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre(9, "Комедия");
        genres.add(genre);
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(4, "R"))
                .genres(genres).build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmService.addFilm(filmTest);
        });

        assertEquals("Не найден Genre с id: 9", exception.getMessage());
    }

    @Test
    @DirtiesContext
    void insertFilmGenres_withNormalBehavior() {
        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre(1, "Комедия");
        Genre genre1 = new Genre(2, "Драма");
        genres.add(genre);
        genres.add(genre1);
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(4, "R"))
                .genres(genres).build();
        filmStorage.addFilm(filmTest);
        filmTest.setGenres(genres);
        filmStorage.insertFilmGenres(filmTest);

        List<Genre> genreList = filmStorage.getGenres(filmTest.getId());

        assertEquals(2, genreList.size());
    }

    @Test
    @DirtiesContext
    void deleteFilmGenres_withNormalBehavior() {
        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre(1, "Комедия");
        Genre genre1 = new Genre(2, "Драма");
        genres.add(genre);
        genres.add(genre1);
        Film filmTest = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2012, 1, 1))
                .duration(90)
                .rate(4)
                .mpa(new Mpa(4, "R"))
                .genres(genres).build();
        filmStorage.addFilm(filmTest);
        filmTest.setGenres(genres);
        filmStorage.insertFilmGenres(filmTest);

        List<Genre> genreList = filmStorage.getGenres(filmTest.getId());

        assertEquals(2, genreList.size());

        filmStorage.deleteFilmGenres(filmTest);
        List<Genre> genreList1 = filmStorage.getGenres(filmTest.getId());

        assertEquals(0, genreList1.size());
    }

    @Test
    @DirtiesContext
    @Order(1)
    void getCommonFilms_withNormalBehavior() {
        User user1 = User.builder().login("user1").email("user1@mail.ru").birthday(LocalDate.of(2000, 1, 1)).build();
        User user2 = User.builder().login("user2").email("user2@mail.ru").birthday(LocalDate.of(2001, 1, 1)).build();

        userService.createUser(user1);
        userService.createUser(user2);

        Film film1 = Film.builder().name("film1").description("desc1").releaseDate(LocalDate.of(1990, 1, 1)).genres(List.of()).rate(0).duration(50).mpa(Mpa.builder().id(1).name("G").build()).build();
        Film film2 = Film.builder().name("film2").description("desc2").releaseDate(LocalDate.of(1995, 1, 1)).genres(List.of()).rate(0).duration(100).mpa(Mpa.builder().id(1).name("G").build()).build();
        Film film3 = Film.builder().name("film3").description("desc3").releaseDate(LocalDate.of(1996, 1, 1)).genres(List.of()).rate(4).duration(150).mpa(Mpa.builder().id(1).name("G").build()).build();
        Film film4 = Film.builder().name("film4").description("desc4").releaseDate(LocalDate.of(1997, 1, 1)).genres(List.of()).rate(5).duration(200).mpa(Mpa.builder().id(1).name("G").build()).build();

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        filmStorage.addFilm(film4);

        filmStorage.makeLike(film1.getId(), user1.getId());
        filmStorage.makeLike(film2.getId(), user2.getId());

        filmStorage.makeLike(film3.getId(), user1.getId());
        filmStorage.makeLike(film3.getId(), user2.getId());
        filmStorage.makeLike(film4.getId(), user1.getId());
        filmStorage.makeLike(film4.getId(), user2.getId());

        Collection<Film> films = filmStorage.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(films, List.of(film4, film3));
    }
}
