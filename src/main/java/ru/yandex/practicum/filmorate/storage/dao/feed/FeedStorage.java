package ru.yandex.practicum.filmorate.storage.dao.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void create(Feed feed);
    List<Feed> get(Long userId);
}
