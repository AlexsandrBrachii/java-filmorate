package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.EventOperation;
import ru.yandex.practicum.filmorate.constants.EventType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public List<Event> getFeed(int userId) {
        if (!eventStorage.getFeed(userId).isEmpty()) {
            return eventStorage.getFeed(userId);
        } else {
            throw new NotFoundException("Хрень");
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
