package ru.yandex.practicum.filmorate.storage.dao.filmDb;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
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
import ru.yandex.practicum.filmorate.storage.dao.directorDb.DirectorDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbStorageTest {
    FilmDbStorage filmDbStorage;
    DirectorDbStorage directorDbStorage;

    @Test
    public void testAddFilmAndGet() {

        Set<Long> likkes = new HashSet<>();
        likkes.add(1L);
        likkes.add(2L);

        Genre genre = new Genre(1L, "qwe");
        Genre genre1 = new Genre(2L, "Фильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        Film film = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 1L, 150L,
                new HashSet<>(likkes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        long id = filmDbStorage.addNewFilm(film).getId();

        Film film1 = filmDbStorage.getFilmByID(film.getId());

        assertThat(film1).hasFieldOrPropertyWithValue("id", id);
    }

    @Test
    public void checkUpdateFilm() {
        Set<Long> likkes = new HashSet<>();
        likkes.add(1L);
        likkes.add(2L);

        Genre genre = new Genre(1L, "qwe");
        Genre genre1 = new Genre(2L, "Фильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        Film film = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 1L, 150L,
                new HashSet<>(likkes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        filmDbStorage.addNewFilm(film);
        film.setName("NEW COLLER");
        filmDbStorage.update(film);

        assertThat(film).hasFieldOrPropertyWithValue("name", "NEW COLLER");

    }

    @Test
    public void checkCompareFilms() {
        Set<Long> likkes = new HashSet<>();
        likkes.add(1L);
        likkes.add(2L);

        Genre genre = new Genre(1L, "qwe");
        Genre genre1 = new Genre(2L, "Фильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        Film film = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 1L, 150L,
                new HashSet<>(likkes),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        Film film1 = filmDbStorage.addNewFilm(film);

        Assertions.assertEquals(film1, film);
    }

    @Test
    public void getMostPopularShouldSuccessfullyCreateThreeFilmsAndGetMostPopularTest() {
        final Set<Long> likes1 = new HashSet<>();
        likes1.add(1L);
        likes1.add(2L);

        final Set<Long> likes2 = new HashSet<>();
        likes1.add(1L);

        final Genre genre = new Genre(1L, "Комедия");
        final Genre genre1 = new Genre(2L, "Драма");
        final Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        final Set<Director> directors = new HashSet<>();
        final Director director = new Director(1L, "Test");
        directorDbStorage.createNewDirector(director);

        directors.add(director);

        final Film firstFilmForTest = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 1L, 150L,
                new HashSet<>(likes2),
                new HashSet<>(genres), new HashSet<>(directors), new Mpa(1L));


        final Film secondFilmForTest = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 2L, 150L,
                new HashSet<>(likes2),
                new HashSet<>(genres), new HashSet<>(directors), new Mpa(1L));

        final Film thirdFilmForTest = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 3L, 150L,
                new HashSet<>(likes1),
                new HashSet<>(genres), new HashSet<>(directors), new Mpa(1L));

        filmDbStorage.addNewFilm(firstFilmForTest);
        filmDbStorage.addNewFilm(secondFilmForTest);
        filmDbStorage.addNewFilm(thirdFilmForTest);

        final List<Film> mostPopularFilmsByGenreIdAndYearFromDB = filmDbStorage.getMostPopular(
                3L, 2L, 2024);

        Assertions.assertNotNull(mostPopularFilmsByGenreIdAndYearFromDB);
    }
}
