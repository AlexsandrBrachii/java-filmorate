package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Component
@Primary
@Slf4j
public class GenreStorageImpl {

    private final JdbcTemplate jdbcTemplate;

    public Collection<Genre> getAllGenres() {
        String sqlGetAllGenres = "SELECT * FROM genres";
        List<Genre> listGenres = jdbcTemplate.query(sqlGetAllGenres, FilmStorageImpl::makeGenre);
        return listGenres;
    }

    public Genre getGenreById(int id) {
        String sqlGetGenre = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genreById = jdbcTemplate.query(sqlGetGenre, FilmStorageImpl::makeGenre, id);
        Genre genre = null;
        if (!genreById.isEmpty()) {
            genre = genreById.get(0);
        }
        return genre;
    }
}