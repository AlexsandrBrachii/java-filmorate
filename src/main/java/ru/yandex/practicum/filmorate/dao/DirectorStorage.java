package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
  Collection<Director> findAll();

  Optional<Director> findById(Integer directorId);

  Director save(Director director);

  Director update(Director director);

  void remove(Integer directorId);

  List<Director> getDirectorsByFilmId(Integer filmId);
}
