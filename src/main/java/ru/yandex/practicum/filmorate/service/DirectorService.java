package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class DirectorService {

  private final DirectorStorage directorRepository;

  public Collection<Director> getAll() {
    return directorRepository.findAll();
  }

  public Director getById(Integer directorId) {
    return directorRepository
        .findById(directorId)
        .orElseThrow(
            () -> {
              String message = String.format("Филь с id %s не найден", directorId);
              return new NotFoundException(message);
            });
  }

  public Director create(Director director) {
    return directorRepository.save(director);
  }

  public Director update(Director director) {
    directorRepository
        .findById(director.getId())
        .orElseThrow(
            () -> {
              String message = String.format("Директор с id %s не найден", director.getId());
              return new NotFoundException(message);
            });
    return directorRepository.update(director);
  }

  public void delete(Integer directorId) {
    directorRepository.remove(directorId);
  }
}
