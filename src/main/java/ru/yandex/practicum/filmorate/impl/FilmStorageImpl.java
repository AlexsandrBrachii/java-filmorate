package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.h2.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Primary
@Slf4j
public class FilmStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    @Override
    public List<Film> getFilm(List<Integer> id) {
        String inSql = String.join(",", Collections.nCopies(id.size(), "?"));
        return new ArrayList<>(Objects.requireNonNull(jdbcTemplate.query(String.format("SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, " +
                "f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration, " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM " +
                "FILMS f " +
                "LEFT JOIN MPA m ON m.MPA_ID = f.MPA_ID " +
                "LEFT JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                "WHERE f.FILM_ID IN (%s)", inSql), id.toArray(), this::makeFilm)));
    }

    @Override
    public Collection<Film> getAllFilms() {

        return jdbcTemplate.query("SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, " +
                "f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration, " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM " +
                "FILMS f " +
                "LEFT JOIN MPA m ON m.MPA_ID = f.MPA_ID " +
                "LEFT JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID", this::makeFilm);
    }

    @Override
    public Film addFilm(Film film) {
        String sqlInsertFilm =
                "INSERT INTO films (name, description, releasedate, duration, rate, mpa_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(sqlInsertFilm, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, film.getDuration());
                    ps.setInt(5, film.getRate() != null ? film.getRate() : 0);
                    ps.setInt(6, film.getMpa().getId());
                    return ps;
                },
                keyHolder);
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdateFilm =
                "UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ?, rate = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(
                sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getRate() != null ? film.getRate() : null,
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        return film;
    }

    @Override
    public String deleteFilm(int filmId) {
        try {
            String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID =?";
            jdbcTemplate.update(sqlQuery, filmId);
            log.info("фильм " + filmId + " удален");
        } catch (DataAccessException e) {
            log.info("фильм " + filmId + " не удален / не найден");
            throw new NotFoundException("фильм с id " + filmId + " не найден");
        }
        return "фильм с id " + filmId + " удалён";
    }

    @Override
    public void makeLike(int idFilm, int idUser) {
        String sqlInsertLikes = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertLikes, idFilm, idUser);
        log.info("User с id=" + idUser + " поставил лайк film с id = " + idFilm);
    }

    @Override
    public void deleteLike(int idFilm, int idUser) {
        String sqlDelete = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, idFilm, idUser);
        log.info("User с id=" + idUser + " удалил свой лайк film с id=" + idFilm);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sqlPopular = "SELECT f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, " +
                "f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration, " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM films f " +
                "LEFT OUTER JOIN film_genres fg ON fg.film_id = f.film_id " +
                "LEFT OUTER JOIN genres g ON g.genre_id = fg.genre_id " +
                "LEFT OUTER JOIN mpa m ON m.mpa_id = f.mpa_id " +
                "LEFT OUTER JOIN LIKES L on f.FILM_ID = L.FILM_ID " +
                "GROUP BY name, genreId, filmId, description, rate, releaseDate, duration " +
                "ORDER BY COUNT(L.USER_ID) " +
                "DESC LIMIT ?";

        Collection<Film> popularFilms = jdbcTemplate.query(sqlPopular, this::makeFilm, count);
        return popularFilms;
    }

    @Override
    public List<Integer> getFilmLikes(Integer id) {
        String sqlFilmLikesById = "SELECT * FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("user_id"), id);
    }

    @Override
    public Mpa checkMpa(Film film) {
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<Mpa> mpaById = jdbcTemplate.query(sqlMpa, FilmStorageImpl::makeMpa, mpaId);
        if (mpaById.isEmpty() || mpaById.get(0) == null || !mpaById.get(0).getId().equals(mpaId)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", mpaId));
        }
        Mpa mpa = mpaById.get(0);
        return mpa;
    }

    @Override
    public List<Genre> getGenres(int idFilm) {
        String sqlGenres =
                "SELECT g.GENRE_ID as genreId, g.GENRE_NAME as genreName FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sqlGenres, FilmStorageImpl::makeGenre, idFilm);
    }

    @Override
    public List<Genre> checkGenre(Film film) {
        List<Genre> filmGenres = new ArrayList<>();
        if (film.getGenres() != null) {
            Set<Integer> genreSet =
                    film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            for (Integer id : genreSet) {
                String sqlGenresId = "SELECT GENRE_ID as genreId, GENRE_NAME as genreName FROM genres WHERE genre_id = ?";
                List<Genre> genresById = jdbcTemplate.query(sqlGenresId, FilmStorageImpl::makeGenre, id);
                if (genresById.isEmpty()
                        || genresById.get(0) == null
                        || !genresById.get(0).getId().equals(id)) {
                    throw new NotFoundException(String.format("Не найден Genre с id: %s", id));
                }
                filmGenres.add(genresById.get(0));
            }
        }
        return filmGenres;
    }

    @Override
    public void insertFilmGenres(Film film) {
        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertFilmGenres, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void deleteFilmGenres(Film film) {
        String sqlDeleteFilmGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilmGenres, film.getId());
    }

    @Override
    public void addDirectorsByFilmId(Collection<Director> directors, Integer filmId) {
        String sqlInsert = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
        String sqlDelete = "DELETE FROM films_directors WHERE film_id = ?";
        List<Object[]> batchArgs = new ArrayList<>();
        directors.forEach(director -> batchArgs.add(new Object[]{filmId, director.getId()}));
        jdbcTemplate.update(sqlDelete, filmId);
        jdbcTemplate.batchUpdate(sqlInsert, batchArgs);
    }

    @Override
    public Collection<Director> findDirectorsByFilmId(Integer filmId) {
        String sqlSelect = "SELECT d.* FROM films_directors fd JOIN directors d ON fd.director_id = d.id WHERE fd.film_id = ?";
        return jdbcTemplate.query(sqlSelect, directorMapper, filmId);
    }

    @Override
    public List<Film> findByDirectorIdAndSortBy(Integer directorId) {
        String sqlSelect = "SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration,  " +
                "m.MPA_ID AS mpaId, m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName, " +
                "COUNT(l.user_id) rate FROM films AS f " +
                "INNER JOIN mpa m ON m.mpa_id = f.mpa_id " +
                "LEFT OUTER JOIN films_directors d ON f.film_id = d.film_id " +
                "LEFT OUTER JOIN likes l ON f.film_id = l.film_id " +
                "LEFT OUTER JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT OUTER JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                " WHERE director_id = ?" +
                "GROUP BY f.film_id";
        return new ArrayList<>(Objects.requireNonNull(jdbcTemplate.query(sqlSelect, this::makeFilm, directorId)));
    }


    public static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {

        String sqlSelect = "SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration,  " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM films AS f " +
                "INNER JOIN mpa m ON m.mpa_id = f.mpa_id " +
                "LEFT OUTER JOIN films_directors d ON f.film_id = d.film_id " +
                "JOIN likes l1 ON l1.film_id = f.film_id AND l1.user_id = ? " +
                "JOIN likes l2 ON l2.film_id = f.film_id AND l2.user_id = ? " +
                "LEFT OUTER JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT OUTER JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                "ORDER BY f.rate DESC";
        return jdbcTemplate.query(sqlSelect, this::makeFilm, userId, friendId);
    }

    @Override
    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        String sql = "SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, " +
                "f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration, " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM " +
                "FILMS f " +
                "LEFT JOIN MPA m ON m.MPA_ID = f.MPA_ID " +
                "LEFT JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                "WHERE true " +
                (genreId != null ? String.format("AND g.GENRE_ID = %d ", genreId) : "") +
                (year != null ? String.format("AND EXTRACT(YEAR FROM f.RELEASEDATE) = %d ", year) : "") +
                "GROUP BY f.FILM_ID " +
                "ORDER BY f.RATE DESC";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Collection<Film> getSearchFilmsByDirector(String query) {
        String sql = "SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, " +
                "f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration, " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM " +
                "directors d " +
                "JOIN films_directors fd ON fd.director_id = d.id " +
                "JOIN films f ON f.film_id = fd.film_id " +
                "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                "WHERE d.name ILIKE '%" + query + "%'";

        return jdbcTemplate.query(sql, this::makeFilm);
    }


    @Override
    public Collection<Film> getSearchFilmsByTitle(String query) {
        String sql = "SELECT " +
                "f.FILM_ID as filmId, " +
                "f.NAME as name, " +
                "f.DESCRIPTION as description, " +
                "f.RATE as rate, " +
                "f.RELEASEDATE as releaseDate, " +
                "f.DURATION as duration, " +
                "m.MPA_ID AS mpaId, " +
                "m.MPA_NAME AS mpaName, " +
                "g.GENRE_ID as genreId, " +
                "g.GENRE_NAME AS genreName " +
                "FROM " +
                "FILMS f " +
                "LEFT JOIN MPA m ON m.MPA_ID = f.MPA_ID " +
                "LEFT JOIN FILM_GENRES fg ON fg.FILM_ID = f.FILM_ID " +
                "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                "WHERE f.name ILIKE '%" + query + "%'";

        return jdbcTemplate.query(sql, this::makeFilm);
    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genreId"))
                .name(rs.getString("genreName"))
                .build();
    }

    public Collection<Film> makeFilm(ResultSet rs) throws SQLException {
        Map<Integer, Film> filmsMap = new LinkedHashMap<>();
        while (rs.next()) {
            int filmId = rs.getInt("filmId");
            Film film = filmsMap.get(filmId);
            if (film == null) {
                Mpa rating = Mpa.builder()
                        .id(rs.getInt("mpaId"))
                        .name(rs.getString("mpaName"))
                        .build();
                film = Film.builder()
                        .id(filmId)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("releaseDate").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .rate(rs.getInt("rate"))
                        .mpa(rating)
                        .genres(new ArrayList<>())
                        .build();
                filmsMap.put(filmId, film);
            }
            String genreName = rs.getString("genreName");
            if (genreName != null) {
                Genre genre = Genre.builder()
                        .id(rs.getInt("genreId"))
                        .name(genreName)
                        .build();
                film.getGenres().add(genre);
            }
        }
        return filmsMap.values();
    }
}