package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.List;

@Component
@Primary
@Slf4j
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getAllGenres() {
        String sqlGetAllGenres = "SELECT * FROM genres";
        List<Genre> listGenres = jdbcTemplate.query(sqlGetAllGenres, FilmDbStorage::makeGenre);
        return listGenres;
    }

    public Genre getGenreById(int id) {
        String sqlGetGenre = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genreById = jdbcTemplate.query(sqlGetGenre, FilmDbStorage::makeGenre, id);
        if (genreById.isEmpty()) {
            throw new NotFoundException("Genre с id=" + id + "не найден");
        }
        Genre genre = genreById.get(0);
        return genre;
    }
}