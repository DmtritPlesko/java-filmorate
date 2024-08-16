package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.filmDb.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.filmDb.FilmStorageInterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class FilmDbService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final FilmStorageInterface filmStorage;
    private final DirectorDbService directorDbService;
    private final FeedService feedService;

    @Autowired
    public FilmDbService(FilmDbStorage filmDbStorage, DirectorDbService directorDbService, FeedService feedService) {
        this.filmStorage = filmDbStorage;
        this.directorDbService = directorDbService;
        this.feedService = feedService;
    }

    public Film addNewFilm(Film film) {
        checkValidation(film);
        return filmStorage.addNewFilm(film);
    }

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
        directorDbService.getDirectorById(id);
        return filmStorage.getFilmBySort(id, sortBy);
    }

    public Film updateFilm(Film film) {
        checkValidation(film);
        return filmStorage.update(film);
    }

    public void takeLike(Long id, Long userId) {
        filmStorage.takeLike(id, userId);
        feedService.create(userId, id, "LIKE", "ADD");
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
        feedService.create(userId, id, "LIKE", "REMOVE");
    }

    public List<Film> search(String query, String by) {
        log.info("Запрос на поиск фильма по названию и/или по режиссёру");
        if (!by.equals("director") && !by.equals("title")
                && !by.equals("director,title") && !by.equals("title,director")) {
            log.error("Параметры запроса переданы неверно");
            throw new NotFoundException("Параметры запроса переданы неверно");
        }

        return filmStorage.allFilms().stream()
                .filter((film -> {
                    if (by.equals("title")) {
                        return film.getName().toLowerCase().contains(query.toLowerCase());
                    } else if (by.equals("director")) {
                        return !film.getDirectors().stream()
                                .filter((director) -> director.getName().toLowerCase().contains(query.toLowerCase()))
                                .toList().isEmpty();
                    } else {
                        return film.getName().toLowerCase().contains(query.toLowerCase()) ||
                                !film.getDirectors().stream()
                                        .filter((director) -> director.getName().toLowerCase().contains(query.toLowerCase()))
                                        .toList().isEmpty();
                    }
                }))
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .toList();
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
