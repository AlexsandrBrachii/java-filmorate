package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getFeedByUserId(int userId);

    void createEvent(Event event);
}
