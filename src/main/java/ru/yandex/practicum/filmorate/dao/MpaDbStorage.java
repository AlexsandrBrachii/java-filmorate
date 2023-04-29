package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;
import java.util.List;

@Component
@Primary
@Slf4j
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getAllMpa() {
        String sqlGetAllMpa = "SELECT * FROM mpa";
        List<Mpa> listMpa = jdbcTemplate.query(sqlGetAllMpa, FilmDbStorage::makeMpa);
        return listMpa;
    }

    public Mpa getMpaById(int id) {
        String sqlGetMpa = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<Mpa> mpaById = jdbcTemplate.query(sqlGetMpa, FilmDbStorage::makeMpa, id);
        Mpa mpa = null;
        if (!mpaById.isEmpty()) {
            mpa = mpaById.get(0);
        }
        return mpa;
    }
}
