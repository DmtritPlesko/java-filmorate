package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.storage.dao.status.StatusDb;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatusService {
    private StatusDb statusStorage;

    public Status getStatusById(Long id) {
        return statusStorage.getStatusById(id);
    }

    public List<Status> getAllStatus() {
        return statusStorage.getAllStatus();
    }
}
