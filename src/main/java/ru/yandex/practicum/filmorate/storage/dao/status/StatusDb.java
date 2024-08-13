package ru.yandex.practicum.filmorate.storage.dao.status;

import ru.yandex.practicum.filmorate.model.Status;

import java.util.List;

public interface StatusDb {
    Status getStatusById(Long id);

    List<Status> getAllStatus();

}
