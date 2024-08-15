package ru.yandex.practicum.filmorate.storage.dao.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;
import ru.yandex.practicum.filmorate.storage.UserStorageInterface;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
class ReviewStorageDBTest {
    private final ReviewStorage storage;
    private final UserStorageInterface userStorage;
    private final FilmStorageInterface filmStorage;

    private User user;
    private Film film;

    @BeforeEach
    void setUp() {
        user = createUser();
        film = createFilm();
    }

    private User createUser() {
        final String uniqueEmailSuffix = UUID.randomUUID().toString();
        final String email = "email" + uniqueEmailSuffix + "@mail.ru";

        final User user = new User();
        user.setEmail(email);
        user.setName("Имя");
        user.setBirthday(LocalDate.now());
        return userStorage.createUser(user);
    }

    private Film createFilm() {
        final Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);

        final Genre genre = new Genre(3L, "qwe");
        final Genre genre1 = new Genre(4L, "Фильм");
        final Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        genres.add(genre1);

        final Film film = new Film();
        film.setName("Cooler2");
        film.setDescription("THIS IS CooLeer");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(150L);
        film.setLikes(likes);
        film.setGenres(genres);
        film.setMpa(new Mpa(1L));
        return filmStorage.addNewFilm(film);
    }

    @Test
    public void shouldCreateReviewWithGivenDetailsSuccessfullyTest() {
        final Review review = new Review();
        review.setContent("not bad");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0L);

        storage.create(review);

        final Review reviewFromDataBase = storage.getReviewById(review.getReviewId());

        assertNotNull(reviewFromDataBase, "Объект не сохранен");
    }

    @Test
    public void shouldUpdateReviewContentAndCheckUpdatedContentTest() {
        final Review review = new Review();
        review.setContent("not bad");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0L);

        storage.create(review);

        final Review reviewForTest = storage.getReviewById(review.getReviewId());
        reviewForTest.setContent("goood");

        storage.update(reviewForTest);

        assertEquals(storage.getReviewById(review.getReviewId()).getContent(), "goood",
                "Описания не равны");
    }

    @Test
    public void shouldDeleteReviewByIdAndVerifyDeletionTest() {
        final Review review = new Review();
        review.setContent("not bad");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0L);

        final List<Review> reviewListBeforeCreateNewReview = storage.getAllReviewsByCount(10);
        storage.create(review);
        storage.delete(review.getReviewId());
        final List<Review> reviewList = storage.getAllReviewsByCount(10);

        assertEquals(reviewList.size(), reviewListBeforeCreateNewReview.size(), "Удаление не произошло");
    }

    @Test
    void shouldRetrieveLastReviewOfFilmByIdAndCheckContentTest() {
        final Review review = new Review();
        review.setContent("not bad");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0L);

        storage.create(review);

        final Review copyReviewFromDataBase = storage.getReviewsByFilmId(film.getId(), 10).getLast();

        assertEquals(review, copyReviewFromDataBase, "Отзывы не равны");
    }

    @Test
    void shouldAddPositiveReactionToReviewAndCheckUpdatedUsefulnessTest() {
        final Review review = new Review();
        review.setContent("not bad");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0L);

        storage.create(review);

        storage.addReaction(review.getReviewId(), review.getUserId(), true);

        final Long usefulRating = storage.getReviewById(review.getReviewId()).getUseful();
        final Long expectedUsefulRating = 1L;

        assertEquals(expectedUsefulRating, usefulRating, "Рейтинг не изменился после добавления реакции");
    }

    @Test
    void shouldRemoveUserReactionFromReviewAndCheckUpdatedUsefulnessTest() {
        final Review review = new Review();
        review.setContent("not bad");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0L);

        storage.create(review);

        storage.addReaction(review.getReviewId(), review.getUserId(), true);
        storage.deleteReaction(review.getReviewId(), review.getUserId());
        final Long usefulRating = storage.getReviewById(review.getReviewId()).getUseful();
        final Long expectedUsefulRating = 0L;

        assertEquals(expectedUsefulRating, usefulRating, "Рейтинг не изменился после удаления реакции");
    }
}