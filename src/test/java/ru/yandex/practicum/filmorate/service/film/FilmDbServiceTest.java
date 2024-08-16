package ru.yandex.practicum.filmorate.service.film;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmDbService;
import ru.yandex.practicum.filmorate.storage.dao.directorDb.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.filmDb.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbServiceTest {
    FilmDbService filmDbService;
    FilmDbStorage filmDbStorage;
    DirectorDbStorage directorDbStorage;

    @BeforeEach
    public void createFilms() {
        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);

        Genre genre = new Genre(4L, "Триллер");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);

        Director director = new Director(1L);
        director.setName("узник");
        directorDbStorage.createNewDirector(director);
        Set<Director> directors = new HashSet<>();
        directors.add(director);

        Film film1 = new Film("филосовский камень", "description",
                LocalDate.now(), 11L, 150L, new HashSet<>(likes),
                new HashSet<>(genres), directors, new Mpa(1L));

        Film film2 = new Film("тайная комната", "description",
                LocalDate.now(), 12L, 150L, new HashSet<>(likes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        Film film3 = new Film("узник азкабана", "description",
                LocalDate.now(), 13L, 150L, new HashSet<>(likes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        Film film4 = new Film("кубок огня", "description",
                LocalDate.now(), 14L, 150L, new HashSet<>(likes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        Film film5 = new Film("орден феникса", "description",
                LocalDate.now(), 15L, 150L, new HashSet<>(likes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        filmDbStorage.addNewFilm(film1);
        filmDbStorage.addNewFilm(film2);
        filmDbStorage.addNewFilm(film3);
        filmDbStorage.addNewFilm(film4);
        filmDbStorage.addNewFilm(film5);
    }

    @Test
    @Order(1)
    @DisplayName("FilmDbService_searchOnTitleAndDirector")
    void searchOnTitleAndDirectorTest() {
        List<Film> films = filmDbService.search("узник", "title,director");
        assertThat(films.size() == 2);
    }

    @Test
    @Order(2)
    @DisplayName("FilmDbService_searchOnDirector")
    void searchOnDirectorTest() {
        List<Film> films = filmDbService.search("узник", "director");
        assertThat(films.size() == 1);
        assertEquals(films.getFirst().getName(), "филосовский камень");
    }

    @Test
    @Order(3)
    @DisplayName("FilmDbService_searchOnDirectorOrDirector")
    void searchOnTitleOrDirectorTest() {
        List<Film> films = filmDbService.search("кубок", "title,director");
        assertThat(films.size() == 1);
        assertEquals(films.getFirst().getName(), "кубок огня");
    }

    @Test
    @Order(4)
    @DisplayName("FilmDbService_searchOnDirectorNotFilm")
    void searchOnDirectorNotFilmTest() {
        List<Film> films = filmDbService.search("орден", "director");
        assertThat(films.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("FilmDbService_searchOnTitle")
    void searchOnTitleTest() {
        List<Film> films = filmDbService.search("аба", "title");
        assertThat(films.size() == 1);
        assertEquals(films.getFirst().getName(), "узник азкабана");
    }
}
