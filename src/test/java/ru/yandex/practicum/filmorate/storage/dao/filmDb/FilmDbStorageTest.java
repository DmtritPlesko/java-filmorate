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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
                new HashSet<Long>(likkes),
                new HashSet<Genre>(genres), new Mpa(1));

        filmDbStorage.addNewFilm(film);

        Film film1 = filmDbStorage.getFilmByID(film.getId());

        assertThat(film1).hasFieldOrPropertyWithValue("id", 4L);
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
                new HashSet<Long>(likkes),
                new HashSet<Genre>(genres), new Mpa(1));

        filmDbStorage.addNewFilm(film);
        film.setName("NEW COLLER");
        filmDbStorage.update(film);

        assertThat(film).hasFieldOrPropertyWithValue("name", "NEW COLLER");

    }

    @Test
    public void checkDeleteFilmAndGetAllFilms() {
        Set<Long> likkes = new HashSet<>();
        likkes.add(1L);
        likkes.add(2L);

        Genre genre = new Genre(1L, "qwe");
        Genre genre1 = new Genre(2L, "Фильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        Film film = new Film("Cooler", "THIS IS CooLeer", LocalDate.now(), 1L, 150L,
                new HashSet<Long>(likkes),
                new HashSet<Genre>(genres), new Mpa(1));

        filmDbStorage.addNewFilm(film);
        List<Film> films = filmDbStorage.allFilms();
        filmDbStorage.deleteFilm(film.getId());

        List<Film> films1 = filmDbStorage.allFilms();
        Assertions.assertNotEquals(films, films1);

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
                new HashSet<Long>(likkes),
                new HashSet<Genre>(genres), new Mpa(1));

        Film film1 = filmDbStorage.addNewFilm(film);

        Assertions.assertEquals(film1, film);
    }
}
