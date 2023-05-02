package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.storage.event.EventOperation;
import ru.yandex.practicum.filmorate.storage.event.EventType;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    int eventId;
    long timestamp;
    int userId;
    EventType eventType;
    EventOperation operation;
    int entityId;
}