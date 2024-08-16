package ru.yandex.practicum.filmorate.storage.dao.filmDb;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorageInterface {

    Film addNewFilm(Film film);

    Film update(Film film);

    Film getFilmByID(Long id);

    List<Film> allFilms();

    void deleteFilmByID(Long id);

    void takeLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> getMostPopular(Long count, Long genreId, Integer year);

    List<Film> getFilmBySort(Long id, List<String> sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);

    void validFilm(long id);
}
