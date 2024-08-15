package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;
import ru.yandex.practicum.filmorate.storage.dao.filmDb.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.userDb.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbService userDbService;


    @Test
    public void getRecommendationsForTwoUsersWithTwoLikesTest() {

        User userOne = User.builder()
                .name("one")
                .email("testone1@mail.ru")
                .years(15)
                .login("one1")
                .birthday(LocalDate.ofYearDay(2015, 1))
                .password("one")
                .build();


        long oneId = userDbStorage.createUser(userOne).getId();

        User userTwo = User.builder()
                .name("two")
                .email("testtwo1@mail.ru")
                .years(15)
                .login("two1")
                .birthday(LocalDate.ofYearDay(2015, 1))
                .password("two")
                .build();

        long twoId = userDbStorage.createUser(userTwo).getId();

        Genre genre = new Genre(1L, "qwe");
        Genre genre1 = new Genre(2L, "Фильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        Film filmTwoLikes = new Film("Two likes", "Two likes", LocalDate.now(), 1L, 150L,
                new HashSet<>(),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        long idTwoLikes = filmDbStorage.addNewFilm(filmTwoLikes).getId();
        filmTwoLikes.setId(idTwoLikes);
        filmDbStorage.takeLike(idTwoLikes, oneId);
        filmDbStorage.takeLike(idTwoLikes, twoId);

        Film filmOneLike = new Film("One like", "One like", LocalDate.now(), 1L, 150L,
                new HashSet<>(),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        long idOneLike = filmDbStorage.addNewFilm(filmOneLike).getId();
        filmOneLike.setId(idOneLike);
        filmDbStorage.takeLike(idOneLike, twoId);

        assertThat(userDbService.getRecommendations(oneId)).hasSize(1);
        assertTrue(userDbService.getRecommendations(oneId).contains(filmOneLike));
    }

    @Test
    public void getRecommendationsForTwoUsersWithNoLikesTest() {

        User userOne = User.builder()
                .name("one")
                .email("testone@mail.ru")
                .years(15)
                .login("one")
                .birthday(LocalDate.ofYearDay(2015, 1))
                .password("one")
                .build();


        long oneId = userDbStorage.createUser(userOne).getId();

        User userTwo = User.builder()
                .name("two")
                .email("testtwo@mail.ru")
                .years(15)
                .login("two")
                .birthday(LocalDate.ofYearDay(2015, 1))
                .password("two")
                .build();

        long twoId = userDbStorage.createUser(userTwo).getId();

        Genre genre = new Genre(1L, "qwe");
        Genre genre1 = new Genre(2L, "Фильм");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        Film filmTwoLikes = new Film("Two likes", "Two likes", LocalDate.now(), 1L, 150L,
                new HashSet<>(),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        long idTwoLikes = filmDbStorage.addNewFilm(filmTwoLikes).getId();
        filmTwoLikes.setId(idTwoLikes);
        filmDbStorage.takeLike(idTwoLikes, twoId);

        Film filmOneLike = new Film("One like", "One like", LocalDate.now(), 1L, 150L,
                new HashSet<>(),
                new HashSet<>(genres), new HashSet<>(), new Mpa(1L));

        long idOneLike = filmDbStorage.addNewFilm(filmOneLike).getId();
        filmOneLike.setId(idOneLike);
        filmDbStorage.takeLike(idOneLike, twoId);

        assertThat(userDbService.getRecommendations(oneId)).hasSize(0);
        assertThat(userDbService.getRecommendations(twoId)).hasSize(0);
    }
}
