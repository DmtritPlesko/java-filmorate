package ru.yandex.practicum.filmorate.storage.dao.rating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingDbStorageTest {
    private final RatingDbStorage ratingDbStorage;

    @Test
    public void getRatingById() {
        Mpa mpa = ratingDbStorage.getRatingById(1L);

        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L);

    }

    @Test
    public void getAllRating() {
        List<Mpa> mpas = ratingDbStorage.getAllRating();

        Assertions.assertEquals(mpas.size(), 5);
    }

}