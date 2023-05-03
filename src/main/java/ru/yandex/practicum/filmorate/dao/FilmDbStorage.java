package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.h2.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.h2.mapper.FilmSortedMapper;
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
public class FilmDbStorage implements FilmStorageDb {

    private final JdbcTemplate jdbcTemplate;

    private final DirectorMapper directorMapper;
    private final FilmSortedMapper filmSortedMapper;

    @Override
    public Film getFilm(int id) {
        String sql =
                "SELECT f.*, m.* FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        Film film = null;
        if (filmRows.next()) {
            Mpa mpa =
                    Mpa.builder().id(filmRows.getInt("mpa_id")).name(filmRows.getString("mpa_name")).build();
            film =
                    Film.builder()
                            .id(id)
                            .name(filmRows.getString("name"))
                            .description(filmRows.getString("description"))
                            .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                            .duration(filmRows.getInt("duration"))
                            .rate(filmRows.getInt("rate"))
                            .mpa(mpa)
                            .build();
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlFilms =
                "SELECT f.*, g.*, m.mpa_name "
                        + "FROM films f "
                        + "LEFT OUTER JOIN film_genres fg ON fg.film_id = f.film_id "
                        + "LEFT OUTER JOIN genres g ON g.genre_id = fg.genre_id "
                        + "LEFT OUTER JOIN mpa m ON m.mpa_id = f.mpa_id";

        Map<Integer, Film> filmMap = new HashMap<>();
        jdbcTemplate.query(
                sqlFilms,
                rs -> {
                    int id = rs.getInt("film_id");
                    Film film = filmMap.get(id);
                    if (film == null) {
                        film =
                                Film.builder()
                                        .id(id)
                                        .name(rs.getString("name"))
                                        .description(rs.getString("description"))
                                        .releaseDate(rs.getDate("releaseDate").toLocalDate())
                                        .duration(rs.getInt("duration"))
                                        .rate(rs.getInt("rate"))
                                        .mpa(FilmDbStorage.makeMpa(rs, 0))
                                        .genres(new ArrayList<>())
                                        .build();
                        filmMap.put(id, film);
                    }

                    Integer genreId = rs.getInt("genre_id");
                    if (genreId != null && genreId != 0) {
                        Genre genre = FilmDbStorage.makeGenre(rs, 0);
                        film.getGenres().add(genre);
                    }
                });
        return filmMap.values();
    }

    @Override
    public Film addFilm(Film film) {
        String sqlInsertFilm =
                "INSERT INTO films (name, description, releaseDate, duration, rate, mpa_id) "
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
                "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rate = ?, mpa_id = ? WHERE film_id = ?";
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
            String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID =?;" +
                    "DELETE FROM FILM_GENRES WHERE FILM_ID=?; " +
                    "DELETE FROM FILMS WHERE FILM_ID =?";
            jdbcTemplate.update(sqlQuery, filmId, filmId, filmId);
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
        log.info("User с id=" + idUser + " поставил лайк film с id=" + idFilm);
    }

    @Override
    public void deleteLike(int idFilm, int idUser) {
        String sqlDelete = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDelete, idFilm, idUser);
        log.info("User с id=" + idUser + " удалил свой лайк film с id=" + idFilm);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sqlPopular = "SELECT f.*, g.*, m.mpa_name " +
                "FROM films f " +
                "LEFT OUTER JOIN film_genres fg ON fg.film_id = f.film_id " +
                "LEFT OUTER JOIN genres g ON g.genre_id = fg.genre_id " +
                "LEFT OUTER JOIN mpa m ON m.mpa_id = f.mpa_id " +
                "WHERE  f.film_id IN (SELECT film_id FROM likes where USER_ID > 0 GROUP BY film_id  ORDER BY COUNT(*)  DESC LIMIT ?)";

        Collection<Film> popularFilms = jdbcTemplate.query(sqlPopular, FilmDbStorage::makeFilm, count);
        if (popularFilms.isEmpty()) {
            popularFilms = getAllFilms().stream().filter(film -> film.getRate() != 0).collect(Collectors.toList());

        }
        for (Film film : popularFilms) {
            film.setGenres(getGenres(film.getId()));
        }
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
        List<Mpa> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::makeMpa, mpaId);
        if (mpaById.isEmpty() || mpaById.get(0) == null || !mpaById.get(0).getId().equals(mpaId)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", mpaId));
        }
        Mpa mpa = mpaById.get(0);
        return mpa;
    }

    @Override
    public List<Genre> getGenres(int idFilm) {
        String sqlGenres =
                "SELECT g.* FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sqlGenres, FilmDbStorage::makeGenre, idFilm);
        return filmGenres;
    }

    @Override
    public List<Genre> checkGenre(Film film) {
        List<Genre> filmGenres = new ArrayList<>();
        if (film.getGenres() != null) {
            Set<Integer> genreSet =
                    film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            for (Integer id : genreSet) {
                String sqlGenresId = "SELECT * FROM genres WHERE genre_id = ?";
                List<Genre> genresById = jdbcTemplate.query(sqlGenresId, FilmDbStorage::makeGenre, id);
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
        List<Object[]> batchArgs = new ArrayList<>();
        directors.forEach(director -> batchArgs.add(new Object[]{filmId, director.getId()}));
        jdbcTemplate.update(FilmSql.DELETE_FILMS_DIRECTORS_BY_FILM_ID, filmId);
        jdbcTemplate.batchUpdate(FilmSql.INSERT_FILMS_DIRECTORS, batchArgs);
    }

    @Override
    public Collection<Director> findDirectorsByFilmId(Integer filmId) {
        return jdbcTemplate.query(FilmSql.SELECT_FILMS_DIRECTORS_JOIN_ON_DIRECTORS, directorMapper, filmId);
    }

    @Override
    public List<Film> findByDirectorIdAndSortByRelateDate(Integer directorId) {
        return jdbcTemplate.query(FilmSql.FILMS_FILTERING_BY_RELEASE_DATE, filmSortedMapper, directorId);
    }

    @Override
    public List<Film> findByDirectorIdAndSortByLikes(Integer directorId) {
        return jdbcTemplate.query(FilmSql.FILMS_FILTERING_BY_LIKES, filmSortedMapper, directorId);
    }

    public static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build();
        return mpa;
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        String sqlGetCommonFilms = "SELECT * " +
                "FROM FILMS f " +
                "JOIN (" +
                "(SELECT film_id FROM LIKES WHERE user_id = ?) " +
                "INTERSECT " +
                "(SELECT film_id FROM LIKES WHERE user_id = ?)" +
                ") q " +
                "ON q.film_id = f.film_id " +
                "LEFT JOIN MPA m ON m.mpa_id = f.mpa_id " +
                "ORDER BY f.rate DESC";
        List<Film> films = jdbcTemplate.query(sqlGetCommonFilms, FilmDbStorage::makeFilm, userId, friendId);
        for (Film film : films) {
            film.setGenres(getGenres(film.getId()));
        }
        return films;
    }

    @Override
    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        StringBuilder sqlWhere = new StringBuilder();
        if (genreId != null) {
            sqlWhere.append(String.format("AND ARRAY_CONTAINS(g.genres_arr, %d) ", genreId));
        }
        if (year != null) {
            sqlWhere.append(String.format("AND EXTRACT(YEAR FROM f.releasedate) = %d ", year));
        }
        String sqlPopularFilms = "SELECT * " +
                "FROM FILMS f " +
                "LEFT JOIN (SELECT film_id, ARRAY_AGG(GENRE_ID) AS genres_arr FROM FILM_GENRES GROUP BY film_id) g " +
                "ON g.film_id = f.film_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE true " + sqlWhere +
                "ORDER BY f.rate DESC";
        List<Film> films = jdbcTemplate.query(sqlPopularFilms, FilmDbStorage::makeFilm);
        for (Film film : films) {
            film.setGenres(getGenres(film.getId()));
        }
        return films;
    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre =
                Genre.builder().id(rs.getInt("genre_id")).name(rs.getString("genre_name")).build();
        return genre;
    }

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa rating = Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build();
        Film film =
                Film.builder()
                        .id(rs.getInt("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("releaseDate").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .rate(rs.getInt("rate"))
                        .mpa(rating)
                        .build();
        return film;
    }

    public List<Film> getRecommendations(List<Integer> recommendedFilmsIds) {
        String inSql = String.join(",", Collections.nCopies(recommendedFilmsIds.size(), "?"));
        List<Film> recommendedFilms = jdbcTemplate.query(String.format("select * " +
                "from films " +
                "join MPA M on M.MPA_ID = FILMS.MPA_ID " +
                "where film_id IN (%s)", inSql), recommendedFilmsIds.toArray(), FilmDbStorage::makeFilm);
        recommendedFilms.forEach(film -> film.setGenres(getGenres(film.getId())));
        return recommendedFilms;
    }


    private static class FilmSql {

        static final String FILMS_FILTERING_BY_RELEASE_DATE =
                "SELECT * FROM films AS f "
                        + "INNER JOIN mpa m ON m.mpa_id = f.mpa_id "
                        + "LEFT OUTER JOIN films_directors d ON f.film_id = d.film_id "
                        + "LEFT OUTER JOIN likes l ON f.film_id = l.film_id "
                        + "WHERE director_id = ? "
                        + "GROUP BY f.film_id "
                        + "ORDER BY f.releasedate";

        static final String FILMS_FILTERING_BY_LIKES =
                "SELECT * FROM films AS f "
                        + "INNER JOIN mpa m ON m.mpa_id = f.mpa_id "
                        + "LEFT OUTER JOIN films_directors d ON f.film_id = d.film_id "
                        + "LEFT OUTER JOIN likes l ON f.film_id = l.film_id "
                        + "WHERE director_id = ? "
                        + "GROUP BY f.film_id "
                        + "ORDER BY COUNT(DISTINCT l.user_id) DESC";
        static final String INSERT_FILMS_DIRECTORS =
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
        static final String SELECT_FILMS_DIRECTORS_JOIN_ON_DIRECTORS =
                "SELECT d.* FROM films_directors fd JOIN directors d ON fd.director_id = d.id WHERE fd.film_id = ?";

        static final String DELETE_FILMS_DIRECTORS_BY_FILM_ID =
                "DELETE FROM films_directors WHERE film_id = ?";
    }
}
