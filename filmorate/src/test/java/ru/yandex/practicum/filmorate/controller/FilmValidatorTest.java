package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.controller.FilmValidator.validateFilm;
import static ru.yandex.practicum.filmorate.controller.UserValidator.validateUser;


@SpringBootTest
public class FilmValidatorTest {



    @Test
    public void validateFilm_WithEmptyName() {
        Film film = new Film( "", "description",
                LocalDate.of(2000, 1, 1), 90);

        try {
            validateFilm(film);
            fail("Ожидаемое исключение ValidationException не было выброшено");
        } catch (ValidationException e) {
            assertEquals("Название фильма не может быть пустым.", e.getMessage());
        }
    }

    @Test
    public void validateFilm_WithDescription201Long() {
        String symbols201 = "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯAAA";
        Film film = new Film( "name", symbols201,
                LocalDate.of(2000, 1, 1), 90);

        try {
            validateFilm(film);
            fail("Ожидаемое исключение ValidationException не было выброшено");
        } catch (ValidationException e) {
            assertEquals("Длина описания не может превышать больше 200 символов.", e.getMessage());
        }
    }

    @Test
    public void validateFilm_WithDescription200Long() {
        String symbols200 = "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯAA";
        Film film = new Film( "name", symbols200,
                LocalDate.of(2000, 1, 1), 90);

        try {
            validateFilm(film);
        } catch (ValidationException e) {
            fail("Исключение ValidationException было выброшено");
        }
    }

    @Test
    public void validateFilm_WithDescription199Long() {
        String symbols199 = "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯA";
        Film film = new Film( "name", symbols199,
                LocalDate.of(2000, 1, 1), 90);

        try {
            validateFilm(film);
        } catch (ValidationException e) {
            fail("Исключение ValidationException было выброшено");
        }
    }

    @Test
    public void validateFilm_WithWrongReleaseDate() {
        Film film = new Film( "name", "description",
                LocalDate.of(1894, 1, 1), 90);

        try {
            validateFilm(film);
            fail("Ожидаемое исключение ValidationException не было выброшено");
        } catch (ValidationException e) {
            assertEquals("Дата релиза не может быть раньше 28 Декабря 1895.", e.getMessage());
        }
    }

    @Test
    public void validateFilm_WithWrongReleaseDateOn1Day() {
        Film film = new Film( "name", "description",
                LocalDate.of(1895, 12, 27), 90);

        try {
            validateFilm(film);
            fail("Ожидаемое исключение ValidationException не было выброшено");
        } catch (ValidationException e) {
            assertEquals("Дата релиза не может быть раньше 28 Декабря 1895.", e.getMessage());
        }
    }

    @Test
    public void validateFilm_WithNormalReleaseDateOn1Day() {
        Film film = new Film( "name", "description",
                LocalDate.of(1895, 12, 29), 90);

        try {
            validateFilm(film);
        } catch (ValidationException e) {
            fail("Исключение ValidationException было выброшено");
        }
    }

    @Test
    public void validateFilm_WithNegativeDuration() {
        Film film = new Film( "name", "description",
                LocalDate.of(2000, 1, 1), -1);

        try {
            validateFilm(film);
            fail("Ожидаемое исключение ValidationException не было выброшено");
        } catch (ValidationException e) {
            assertEquals("Продолжительность фильма должна быть положительной.", e.getMessage());
        }
    }





}
