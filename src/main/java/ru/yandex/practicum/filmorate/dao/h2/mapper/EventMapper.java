package ru.yandex.practicum.filmorate.dao.h2.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.event_enum.EventOperation;
import ru.yandex.practicum.filmorate.dao.event_enum.EventType;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
