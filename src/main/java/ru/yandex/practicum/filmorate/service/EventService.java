package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event_enum.EventOperation;
import ru.yandex.practicum.filmorate.dao.event_enum.EventType;
import ru.yandex.practicum.filmorate.impl.UserStorageImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.dao.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorageImpl userDbStorage;

    public List<Event> getFeed(int userId) {
        if (userDbStorage.getUser(userId) != null) {
            return eventStorage.getFeedByUserId(userId);
        } else {
            throw new NotFoundException("Not found");
        }
    }

    public void createEvent(int userId, EventType eventType, EventOperation eventOperation, int entityId) {
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(eventType)
                .operation(eventOperation)
                .entityId(entityId)
                .build();

        eventStorage.createEvent(event);
    }
}
