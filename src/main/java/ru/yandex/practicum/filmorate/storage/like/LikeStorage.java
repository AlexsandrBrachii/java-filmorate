package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {

    void addLikeToFilm(Integer filmId, Integer userId);

    void deleteLikeFromFilm(Integer filmId, Integer userId);
}