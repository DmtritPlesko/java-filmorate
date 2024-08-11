package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Feed {
    private long userId;
    private long entityId;
    private String eventType;
    private String operation;
    private long timestamp;
}
