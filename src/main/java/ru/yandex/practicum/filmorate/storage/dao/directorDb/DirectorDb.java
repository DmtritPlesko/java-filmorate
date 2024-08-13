package ru.yandex.practicum.filmorate.storage.dao.directorDb;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDb {

    List<Director> allDirectors();

    Director createNewDirector(Director director);

    Director getDirectorById(Long id);

    Director updateDirector(Director director);

    void deleteDirectorById(Long id);

}
