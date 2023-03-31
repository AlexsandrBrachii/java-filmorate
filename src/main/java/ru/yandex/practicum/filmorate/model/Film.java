package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
public class Film {

    private Integer id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
    private MPA mpa;
    private List<Genre> genre;
    private Set<Long> likes;

}

/*    INSERT INTO genres (name) VALUES
('Комедия'),
        ('Драма'),
        ('Мультфильм'),
        ('Триллер'),
        ('Документальный'),
        ('Боевик');

        INSERT INTO mpa (name) VALUES
        ('G'),
        ('PG'),
        ('PG-13'),
        ('R'),
        ('NC-17');*/

/*{
        "name": "nisi eiusmod",
        "description": "adipisicing",
        "releaseDate": "1967-03-25",
        "duration": 100,
        "mpa": { "id": 1}
        }
        вот такой json приходит на вход с помощью аннотации @RequestBody он заходит в этот метод
@Override
public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, releaseDate, duration, genre, mpa) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, film.getName());
        ps.setString(2, film.getDescription());
        ps.setDate(3, Date.valueOf(film.getReleaseDate()));
        ps.setInt(4, film.getDuration());
        ps.setArray(5, (Array) film.getGenre());
        ps.setArray(6, (Array) film.getMpa());
        return ps;
        }, keyHolder);

        Integer id = keyHolder.getKey().intValue();
        film.setId(id);
        return film;
        }
        и после чего выбрасывает эту ошибку
        2023-03-28 16:25:26.926  WARN 13764 --- [nio-8080-exec-3] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `java.util.HashSet<java.lang.Integer>` from Object value (token `JsonToken.START_OBJECT`); nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.util.HashSet<java.lang.Integer>` from Object value (token `JsonToken.START_OBJECT`)<EOL> at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 6, column: 10] (through reference chain: ru.yandex.practicum.filmorate.model.Film["mpa"])]

        как мне исправить эту ошибку?*/

