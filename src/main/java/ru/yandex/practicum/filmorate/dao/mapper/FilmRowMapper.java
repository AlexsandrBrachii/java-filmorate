package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String genresString = rs.getString("genres");
        if (genresString != null) {
            String[] genreStrings = genresString.split(";");
            for (String genreString : genreStrings) {
                String[] genreData = genreString.split(",");
                try {
                    final Genre genre = Genre.builder()
                        .id(Integer.parseInt(genreData[0]))
                        .name(genreData[1])
                        .build();
                    genres.add(genre);
                } catch (NumberFormatException e) {
                    break;
                }
            }
        }
        List<Director> directors = new ArrayList<>();
        String directorsString = rs.getString("directors");
        if (directorsString != null) {
            String[] directorStrings = directorsString.split(";");
            for (String directorString : directorStrings) {
                String[] directorData = directorString.split(",");
                try {
                    final Director director = Director.builder()
                        .id(Integer.parseInt(directorData[0]))
                        .name(directorData[1])
                        .build();
                    directors.add(director);
                } catch (NumberFormatException e) {
                    break;
                }
            }
        }

        return Film.builder()
            .id(rs.getInt("film_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .releaseDate(rs.getDate("releasedate").toLocalDate())
            .duration(rs.getInt("duration"))
            .rate(rs.getInt("likes_count"))
            .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa")).build())
            .genres(genres)
            .directors(directors)
            .build();
    }
}
