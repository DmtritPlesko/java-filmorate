package ru.yandex.practicum.filmorate.storage.filmstorage;

import ru.yandex.practicum.filmorate.dto.Film;

import java.util.*;

public interface FilmStorageInterface {

    void addNewFilm(Film film);

    void update(Film film);

    Film getFilmByID(Long id);

    Collection<Film> allFilms();

    void takeLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);
}
