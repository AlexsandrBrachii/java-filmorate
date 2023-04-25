package ru.yandex.practicum.filmorate.dao;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserService userService;
    private final FilmService filmService;

    @Test
    void getCommonFilms_withNormalBehavior() {
        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        User user2 = User.builder()
                .login("friend")
                .name("friend adipisicing")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1976, 8, 20))
                .build();

        User user3 = User.builder()
                .login("common")
                .name("friend adipisicing")
                .email("friend@common.ru")
                .birthday(LocalDate.of(2000, 8, 20))
                .build();

        Film film1 = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .mpa(new MPA(1, null))
                .build();

        Film film2 = Film.builder()
                .name("New film")
                .description("New film about friends")
                .releaseDate(LocalDate.of(1999, 4, 30))
                .duration(120)
                .mpa(new MPA(3, null))
                .genres(new ArrayList<>(List.of(new Genre(1, null))))
                .build();

        Film film3 = Film.builder()
                .name("New film with director")
                .description("Film with director")
                .releaseDate(LocalDate.of(1999, 4, 30))
                .duration(120)
                .mpa(new MPA(3, null))
                .genres(new ArrayList<>(List.of(new Genre(1, null))))
                .build();

        List<Film> films = List.of(film1, film2, film3);
        List<User> users = List.of(user1, user2, user3);
        films.forEach(filmService::addFilm);
        users.forEach(userService::createUser);
        filmService.makeLike(3, 1);
        filmService.makeLike(3, 2);
        filmService.makeLike(2, 2);
        Optional<List<Film>> optionalRecommendations = Optional.ofNullable(userService.getRecommendations(1));
        assertThat(optionalRecommendations)
                .isPresent()
                .hasValueSatisfying(filmsFromDb -> assertThat(filmsFromDb.get(0).getName().equals("New film")).isTrue());
    }

}
