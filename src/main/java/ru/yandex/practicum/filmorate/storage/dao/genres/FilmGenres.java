package ru.yandex.practicum.filmorate.storage.dao.genres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenres {
    Genre getGenresById(Long id);

    List<Genre> getAllGenres();

}
