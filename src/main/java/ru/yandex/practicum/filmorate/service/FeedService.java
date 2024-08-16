package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.dao.feed.FeedStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;

    public void create(Long userId, Long entityId, String eventType, String operation) {
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        feedStorage.create(feed);
    }

    public List<Feed> getFeed(Long userId) {
        return feedStorage.get(userId);
    }
}
