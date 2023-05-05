package ru.yandex.practicum.filmorate.dao.h2.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
            .id(rs.getInt("film_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .rate(rs.getInt("rate"))
            .releaseDate(rs.getDate("releasedate").toLocalDate())
            .duration(rs.getInt("duration"))
            .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build())
            .build();
    }
}
