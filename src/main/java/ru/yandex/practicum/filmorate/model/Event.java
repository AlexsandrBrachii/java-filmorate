package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dao.event_enum.EventOperation;
import ru.yandex.practicum.filmorate.dao.event_enum.EventType;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Integer eventId;
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Integer entityId;
}