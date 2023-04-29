package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    private Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(value = "/{id}")
    private Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping(value = "/popular")
    private Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    private Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    private Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public String deleteFilm(@PathVariable Integer id) {
        return filmService.deleteFilm(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    private void makeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.makeLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    private void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmsByDirector(
            @PathVariable("id") Integer directorId, @RequestParam(name = "sortBy") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping(value = "/common")
    private Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}