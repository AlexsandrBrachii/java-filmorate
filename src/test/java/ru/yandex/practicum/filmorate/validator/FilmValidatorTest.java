package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@SpringBootTest
public class FilmValidatorTest {

    @Test
    public void validateFilm_WithEmptyName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateFilm(film);
        });

        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    public void validateFilm_WithDescription201Long() {
        String symbols201 = "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯAAA";

        Film film = Film.builder()
                .id(1)
                .name("name")
                .description(symbols201)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateFilm(film);
        });

        assertEquals("Длина описания не может превышать больше 200 символов.", exception.getMessage());
    }

    @Test
    public void validateFilm_WithDescription200Long() {
        String symbols200 = "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯAA";

        Film film = Film.builder()
                .id(1)
                .name("name")
                .description(symbols200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90).build();

        assertDoesNotThrow(() -> validateFilm(film));
    }

    @Test
    public void validateFilm_WithDescription199Long() {
        String symbols199 = "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯ" +
                "аАбБвВгГдДеЕёЁжЖзЗиИйЙкКлЛмМнНоОпПрРсСтТуУфФхХцЦчЧшШщЩъЪыЫьЬэЭюЮяЯA";

        Film film = Film.builder()
                .id(1)
                .name("name")
                .description(symbols199)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90).build();

        assertDoesNotThrow(() -> validateFilm(film));
    }

    @Test
    public void validateFilm_WithWrongReleaseDate() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1894, 1, 1))
                .duration(90).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateFilm(film);
        });

        assertEquals("Дата релиза не может быть раньше 28 Декабря 1895.", exception.getMessage());
    }

    @Test
    public void validateFilm_WithWrongReleaseDateOn1Day() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(90).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateFilm(film);
        });

        assertEquals("Дата релиза не может быть раньше 28 Декабря 1895.", exception.getMessage());
    }

    @Test
    public void validateFilm_WithNormalReleaseDateOn1Day() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(90).build();

        assertDoesNotThrow(() -> validateFilm(film));
    }

    @Test
    public void validateFilm_WithNegativeDuration() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-1).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validateFilm(film);
        });

        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }
}