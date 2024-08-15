package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.directorDb.DirectorDb;
import ru.yandex.practicum.filmorate.storage.dao.directorDb.DirectorDbStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorDbService {
    private final DirectorDb directorDbStorage;

    @Autowired
    public DirectorDbService(DirectorDbStorage directorDb) {
        this.directorDbStorage = directorDb;
    }

    public Director createDirector(Director director) {
        if (director.getName().isBlank()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
        return directorDbStorage.createNewDirector(director);
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.allDirectors();
    }

    public Director getDirectorById(Long id) {
        return directorDbStorage.getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        return directorDbStorage.updateDirector(director);
    }

    public void deleteDirectorById(Long id) {
        getDirectorById(id);
        directorDbStorage.deleteDirectorById(id);
    }

}
