package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final FilmStorageInterface filmStorage;

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

    public List<Film> getMostPopular(Long count, Long genreId, Integer year) {
        return filmStorage.getMostPopular(count, genreId, year);
    }

    public List<Film> getFilmBySort(Long id, List<String> sortBy) {
        return filmStorage.getFilmBySort(id, sortBy);
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
    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> search(String query, String by) {
        log.info("Запрос на поиск фильма по названию и/или по режиссёру");
        if (!by.equals("director") && !by.equals("title")
                && !by.equals("director,title") && !by.equals("title,director")) {
            log.error("Параметры запроса переданы неверно");
            throw new NotFoundException("Параметры запроса переданы неверно");
        }
        return filmStorage.search(query, by);

//        List<Film> filteredFilms;
//        if (by.equals("director")) {
//            log.info("Поиск фильма по режиссёру");
//            filteredFilms = filmStorage.allFilms()
//                    .stream().filter(film -> film.getDirectors().stream()
//                    .anyMatch(director -> director.getName().contains(query))).toList();
//        } else if (by.equals("title")) {
//            log.info("Поиск фильма по названию");
//            filteredFilms = filmStorage.allFilms().stream()
//                    .filter(film -> film.getName().contains(query)).toList();
//        } else {
//            log.info("Поиск фильма по названию и по режиссёру");
//            filteredFilms = filmStorage.allFilms()
//                    .stream().filter(film -> film.getName().contains(query) ||
//                            film.getDirectors().stream()
//                                    .anyMatch(director -> director.getName().contains(query))).toList();
//        }
//        return filteredFilms;
    }


    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmByID(id);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
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
