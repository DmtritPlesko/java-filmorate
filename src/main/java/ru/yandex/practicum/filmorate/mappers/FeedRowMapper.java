package ru.yandex.practicum.filmorate.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper {
    public static Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setUserId(resultSet.getLong("user_id"));
        feed.setEntityId(resultSet.getLong("entity_id"));
        feed.setEventType(resultSet.getString("event_type"));
        feed.setOperation(resultSet.getString("operation"));
        feed.setTimestamp(resultSet.getTimestamp("time_stamp").toInstant().toEpochMilli());
        return feed;
    }
}
