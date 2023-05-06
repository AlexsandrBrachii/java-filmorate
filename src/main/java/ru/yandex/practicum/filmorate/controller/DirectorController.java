package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

  private final DirectorService directorService;

  @GetMapping
  public Collection<Director> allDirectors() {
    return directorService.getAll();
  }

  @GetMapping("/{id}")
  public Director directorById(@PathVariable("id") Integer directorId) {
    return directorService.getById(directorId);
  }

  @PostMapping
  public Director createDirector(@Valid @RequestBody Director director) {
    return directorService.create(director);
  }

  @PutMapping
  public Director updateDirector(@Valid @RequestBody Director director) {
    return directorService.update(director);
  }

  @DeleteMapping("/{id}")
  public void deleteDirector(@PathVariable("id") Integer directorId) {
    directorService.delete(directorId);
  }
}
