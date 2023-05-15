package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageImplTest {

    private final UserService userService;
    private final FilmService filmService;
    private final FilmStorage filmStorage;

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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addFilm(filmTest));

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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addFilm(filmTest));

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
    void deleteFilm_withNormalBehavior() {
        Film film1 = Film.builder().name("film1").description("desc1").releaseDate(LocalDate.of(1990, 1,
                1)).genres(List.of()).rate(0).duration(50).mpa(Mpa.builder().id(1).name("G").build()).build();
        filmStorage.addFilm(film1);
        List<Film> filmsBeforeDelete = new ArrayList<>(filmStorage.getAllFilms());
        assertEquals(1, filmsBeforeDelete.size());

        filmStorage.deleteFilm(1);
        List<Film> filmsAfterDelete = new ArrayList<>(filmStorage.getAllFilms());
        assertEquals(0, filmsAfterDelete.size());
    }
}
