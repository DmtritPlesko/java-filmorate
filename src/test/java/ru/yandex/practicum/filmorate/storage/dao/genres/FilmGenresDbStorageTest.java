package ru.yandex.practicum.filmorate.storage.dao.genres;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenresDbStorageTest {
    private final FilmGenresDbStorage filmStorage;

    @Test
    public void getGenreById() {
        final Genre genre = filmStorage.getGenresById(1L);

        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L);

    }

    @Test
    public void getAllGenre() {
        List<Genre> genres = filmStorage.getAllGenres();

        Assertions.assertEquals(genres.size(), 6);
    }

}