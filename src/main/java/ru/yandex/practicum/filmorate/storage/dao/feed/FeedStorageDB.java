package ru.yandex.practicum.filmorate.storage.dao.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeedStorageDB implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Feed feed) {
        log.info("Добавление нового события {}", feed);
        final String sqlQuery = "INSERT INTO feeds (user_id, entity_id, event_type, operation, timestamp) " +
                "VALUES (?, ?, ?, ?, ?);";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement pr = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
                pr.setLong(1, feed.getUserId());
                pr.setLong(2, feed.getEntityId());
                pr.setString(3, feed.getEventType());
                pr.setString(4, feed.getOperation());
                pr.setLong(5, feed.getTimestamp());
                return pr;
            }, keyHolder);

            Number generatedKey = keyHolder.getKey();
            assert generatedKey != null;
            feed.setEventId(generatedKey.longValue());
        } catch (DataAccessException e) {
            log.error("Ошибка при добавлении события: ", e);
            throw new NotFoundException("Неверные данные запроса");
        }
    }

    @Override
    public List<Feed> get(Long userId) {
        if (userId == null || userId <= 0) {
            throw new NotFoundException("User с id=" + userId + " не найден");
        }
        log.info("Fetching feed for user with id {}", userId);
        final String sqlQuery = "SELECT * FROM feeds WHERE user_id = ?";
        List<Feed> feed = jdbcTemplate.query(sqlQuery, FeedRowMapper::mapRow, userId);
        log.info("Fetched {} events", feed.size());
        return feed;
    }
}
