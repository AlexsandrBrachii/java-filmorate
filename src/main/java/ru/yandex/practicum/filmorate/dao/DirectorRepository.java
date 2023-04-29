package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorRepository {
  Collection<Director> findAll();

  Optional<Director> findById(Integer directorId);

  Director save(Director director);

  Director update(Director director);

  void remove(Integer directorId);
}
