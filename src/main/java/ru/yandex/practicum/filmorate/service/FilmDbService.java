package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;
import ru.yandex.practicum.filmorate.storage.dao.filmDb.FilmDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class FilmDbService {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private FilmStorageInterface filmStorage;


    @Autowired
    public FilmDbService(FilmDbStorage filmDbStorage) {
        this.filmStorage = filmDbStorage;
    }

    //create
    public Film addNewFilm(Film film) {
        checkValidation(film);
        return filmStorage.addNewFilm(film);
    }

    //read
    public Film getFilmById(Long id) {
        return filmStorage.getFilmByID(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.allFilms();
    }

    public List<Film> getPopularFilm(Long limit) {
        return filmStorage.getPopularFilm(limit);
    }


    //update
    public Film updateFilm(Film film) {
        checkValidation(film);
        return filmStorage.update(film);
    }

    public void takeLike(Long id, Long userId) {
        filmStorage.takeLike(id, userId);
    }

    //delete
    public void deleteFilmById(Long id) {
        filmStorage.deleteFilm(id);
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
    }

    private void checkValidation(Film film) {
        if (film.getName().isBlank()) {
            log.error("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else if ((film.getReleaseDate().isBefore(LocalDate.parse("28.12.1895", formatter)))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() < 0) {
            log.error("Продолжительность не может быть отрицательной");
            throw new ValidationException("Продолжительность не может быть отрицательной");
        }
    }
}
