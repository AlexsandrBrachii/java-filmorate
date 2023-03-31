package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Array;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*@Override
    public Film getFilm(int id) {

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);

        Film film = null;
        if (filmRows.next()) {
            film = Film.builder().id(id)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .genre(Collections.singleton(filmRows.getInt("genre_id")))
                    .mpa(Collections.singleton(filmRows.getInt("mpa_id")))
                    .build();
        } else {
            log.info("film не найден");
        }
        return film;
    }*/
    @Override
    public Film getFilm(int id) {

        /*SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);

        Film film = null;
        if (filmRows.next()) {
            Integer genreId = filmRows.getInt("genre_id");
            Genre genre = Genre.builder().id(genreId).build();

            Integer mpaId = filmRows.getInt("mpa_id");
            MPA mpa = MPA.builder().id(mpaId).build();

            film = Film.builder().id(id)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .genre((List<Genre>) genre)
                    .mpa(mpa)
                    .build();
        } else {
            log.info("фильм не найден");
        }
        return film;*/
        return null;
    }


    /*@Override
    public Collection<Film> getAllFilms() {

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films");

        Collection<Film> films = new ArrayList<>();
        while (filmRows.next()) {
            Film film = Film.builder().id(filmRows.getInt("film_id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                    .duration(filmRows.getInt("duration")).build();
            films.add(film);
        }
        return films;
    }*/

    @Override
    public Collection<Film> getAllFilms() {
        /*String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, "
                + "  g.genre_id, m.mpa_id, l.like_id "
                + "FROM films f "
                + "JOIN genres g ON f.genre_id = g.genre_id "
                + "JOIN mpa m ON f.mpa_id = m.mpa_id "
                + "JOIN likes l ON f.likes_id = l.like_id";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql);

        Map<Integer, Film> filmsMap = new HashMap<>();
        while (filmRows.next()) {
            int filmId = filmRows.getInt("film_id");
            Film film = filmsMap.getOrDefault(filmId, Film.builder()
                    .id(filmId)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    //.genre(new HashSet<>())
                    //.mpa(new HashSet<>())
                    .likes(new HashSet<>())
                    .build());

            //film.getGenre().add(filmRows.getInt("genre_id"));
            //film.getMpa().add(filmRows.getInt("mpa_id"));
            film.getLikes().add(filmRows.getLong("like_id"));

            filmsMap.put(filmId, film);
        }
        return filmsMap.values();*/
        return null;
    }


    @Override
    public Film addFilm(Film film) {

        String sqlInsertFilm = "INSERT INTO films (name, description, releaseDate, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlInsertFilm, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);

        return film;
    }

    // Добавление записей в таблицу film_genres для каждого жанра фильма
        /*String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenre()) {
            jdbcTemplate.update(sqlInsertFilmGenres, filmId, genre.getId());
        }*/

    @Override
    public Film updateFilm(Film film) {
        return null;
    }
}
