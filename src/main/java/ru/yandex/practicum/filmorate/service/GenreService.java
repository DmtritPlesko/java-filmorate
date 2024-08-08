package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genres.FilmGenres;
import ru.yandex.practicum.filmorate.storage.dao.genres.FilmGenresDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private FilmGenres filmGenresStorage;

    @Autowired
    public GenreService(FilmGenresDbStorage filmGenresDbStorage) {
        this.filmGenresStorage = filmGenresDbStorage;
    }

    public Genre getGenresByID(Long id) {
        if (id > 6) {
            throw new NotFoundException("Нет жанра с таким айди");
        }
        return filmGenresStorage.getGenresById(id);
    }

    public List<Genre> getAllGenres() {
        return filmGenresStorage.getAllGenres();
    }
}
