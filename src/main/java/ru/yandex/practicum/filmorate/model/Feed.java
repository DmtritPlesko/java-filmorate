package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    @NotNull
    private Long eventId;
    @NotNull
    private Long userId;
    @NotNull
    private Long entityId;
    @NotNull
    private String eventType;
    @NotNull
    private String operation;
    @NotNull
    private Long timestamp;
}
