package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public Collection<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        Genre genre = genreDbStorage.getGenreById(id);
        if (genre == null) {
            throw new NotFoundException("Genre с id=" + id + "не найден");
        }
        return genre;
    }
}