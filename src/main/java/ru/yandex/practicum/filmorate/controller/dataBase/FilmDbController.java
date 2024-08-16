package ru.yandex.practicum.filmorate.controller.dataBase;

import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmDbController {
    private final FilmDbService filmService;

    @Autowired
    public FilmDbController(FilmDbService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addNewFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getAllFilm() {
        return filmService.getAllFilms();
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") Long id) {
        filmService.deleteFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void takeLike(@PathVariable("id") Long filmId,
                         @PathVariable("userId") Long userId) {
        filmService.takeLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId,
                           @PathVariable("userId") Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") Long count,
                                     @RequestParam(value = "genreId", required = false) Long genreId,
                                     @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getMostPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> filmBySort(@PathVariable("directorId") Long id,
                                 @RequestParam List<String> sortBy) {
        return filmService.getFilmBySort(id, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam @NotBlank String query,
                             @RequestParam(required = false, defaultValue = "title") String by) {
        return filmService.search(query, by);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
