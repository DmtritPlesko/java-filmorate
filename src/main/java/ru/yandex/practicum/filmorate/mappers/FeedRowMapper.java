package ru.yandex.practicum.filmorate.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper {
    public static Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getLong("event_id"))
                .userId(resultSet.getLong("user_id"))
                .entityId(resultSet.getLong("entity_id"))
                .eventType(resultSet.getString("event_type"))
                .operation(resultSet.getString("operation"))
                .timestamp(resultSet.getLong("timestamp"))
                .build();
    }
}
