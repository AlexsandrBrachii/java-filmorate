package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public Film getFilm(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        Film film = null;
        if (filmRows.next()) {
            Integer mpaId = filmRows.getInt("mpa_id");
            String sqlMpa = "SELECT * FROM mpa WHERE mpa_id = ?";
            List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::makeMpa, mpaId);
            MPA mpa = mpaById.get(0);
            film = Film.builder().id(id)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(mpa)
                    .build();
        } else {
            throw new NotFoundException("film с id=" + id + "не найден");
        }
        String sqlGenre = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        RowMapper<Integer> rowMapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("genre_id");
            }
        };
        List<Integer> idGenre = jdbcTemplate.query(sqlGenre, new Object[]{id}, rowMapper);
        List<Genre> filmGenres = new ArrayList<>();
        for (Integer idG : idGenre) {
            String sqlGenresId = "SELECT * FROM genres WHERE genre_id = ?";
            List<Genre> genresById = jdbcTemplate.query(sqlGenresId, FilmDbStorage::makeGenre, idG);
            if (genresById.isEmpty() || genresById.get(0) == null || !genresById.get(0).getId().equals(idG)) {
                throw new NotFoundException(String.format("Не найден Genre с id: %s", idG));
            }
            filmGenres.add(genresById.get(0));
        }
        film.setGenres(filmGenres);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlFilmById = "SELECT * FROM films LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id";
        List<Film> films = jdbcTemplate.query(sqlFilmById, FilmDbStorage::makeFilm);
        for (Film film : films) {
            String sqlFilmGenres = "SELECT * " +
                    "FROM film_genres LEFT OUTER JOIN genres " +
                    "ON genres.genre_id = film_genres.genre_id " +
                    "WHERE film_id = ?";
            List<Genre> filmGenres = jdbcTemplate.query(sqlFilmGenres, FilmDbStorage::makeGenre, film.getId());
            film.setGenres(filmGenres);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::makeMpa, mpaId);
        if (mpaById.isEmpty() || mpaById.get(0) == null || !mpaById.get(0).getId().equals(mpaId)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", mpaId));
        }
        MPA mpa = mpaById.get(0);
        if (film.getGenres() != null) {
            Set<Integer> genreSet = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            List<Genre> filmGenres = new ArrayList<>();
            for (Integer id : genreSet) {
                String sqlGenresId = "SELECT * FROM genres WHERE genre_id = ?";
                List<Genre> genresById = jdbcTemplate.query(sqlGenresId, FilmDbStorage::makeGenre, id);
                if (genresById.isEmpty() || genresById.get(0) == null || !genresById.get(0).getId().equals(id)) {
                    throw new NotFoundException(String.format("Не найден Genre с id: %s", id));
                }
                filmGenres.add(genresById.get(0));
            }
            film.setGenres(filmGenres);
        }
        film.setMpa(mpa);
        String sqlInsertFilm = "INSERT INTO films (name, description, releaseDate, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
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
        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertFilmGenres, filmId, genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::makeMpa, mpaId);
        if (mpaById.isEmpty() || mpaById.get(0) == null || !mpaById.get(0).getId().equals(mpaId)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", mpaId));
        }
        MPA mpa = mpaById.get(0);
        if (film.getGenres() != null) {
            Set<Integer> genreSet = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            List<Genre> filmGenres = new ArrayList<>();
            for (Integer id : genreSet) {
                String sqlGenresId = "SELECT * FROM genres WHERE genre_id = ?";
                List<Genre> genresById = jdbcTemplate.query(sqlGenresId, FilmDbStorage::makeGenre, id);
                if (genresById.isEmpty() || genresById.get(0) == null || !genresById.get(0).getId().equals(id)) {
                    throw new NotFoundException(String.format("Не найден Genre с id: %s", id));
                }
                filmGenres.add(genresById.get(0));
            }
            film.setGenres(filmGenres);
        }
        film.setMpa(mpa);
        if (getFilm(film.getId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        String sqlUpdateFilm = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        String sqlDeleteFilmGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilmGenres, film.getId());
        if (film.getGenres() != null) {
            String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertFilmGenres, film.getId(), genre.getId());
            }
        }
        return film;
    }

    public void makeLike(int idFilm, int idUser) {
        String sqlInsertLikes = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertLikes, idFilm, idUser);
        log.info("User с id=" + idUser + " поставил лайк film с id=" + idFilm);
    }

    public void deleteLike(int idFilm, int idUser) {
        userDbStorage.getUser(idUser);
        String sqlDelete = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, idFilm, idUser);
        log.info("User с id=" + idUser + " удалил свой лайк film с id=" + idFilm);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count < 1) {
            log.info("count не может быть отрицательным.");
            throw new NotFoundException("count не может быть отрицательным.");
        }
        String sqlPopular = "SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(*) DESC LIMIT ?";
        List<Integer> popularFilmId = jdbcTemplate.queryForList(sqlPopular, Integer.class, count);
        Collection<Film> popularFilms = new ArrayList<>();
        if (popularFilmId.isEmpty()) {
            popularFilms = getAllFilms();
        } else {
            for (Integer id : popularFilmId) {
                popularFilms.add(getFilm(id));
            }
        }
        return popularFilms;
    }

    public List<Integer> getFilmLikes(Integer id) {
        String sqlFilmLikesById = "SELECT * FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("user_id"), id);
    }

    public static MPA makeMpa(ResultSet rs, int rowNum) throws SQLException {
        MPA mpa = MPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name")).build();
        return mpa;
    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name")).build();
        return genre;
    }

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        MPA rating = MPA.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build();
        Film film = Film.builder().id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .mpa(rating).build();

        return film;
    }
}