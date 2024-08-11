package ru.yandex.practicum.filmorate.storage.dao.filmDb;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenresMapper;
import ru.yandex.practicum.filmorate.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;
import ru.yandex.practicum.filmorate.storage.dao.genres.FilmGenresDbStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Setter
@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorageInterface {

    private int review_id = 1;
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresDbStorage genresDbStorage;
    private final String save = "INSERT INTO feeds (user_id, entity_id, event_type, operation, time_stamp) " +
            "values (?, ?, ?, ?, ?)";

    @Override
    public Film addNewFilm(Film film) {
        log.info("Добавление нового фильма в БД");

        final String sqlQuery = "INSERT INTO films (name, description, releaseDate, duration,mpa_id) VALUES (?, ?, ?, ?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Number number = keyHolder.getKey();
        film.setId(number.longValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                boolean exists = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM genres WHERE genre_id = ?", Integer.class, genre.getId()) > 0;

                if (!exists) {
                    String insertGenreSql = "INSERT INTO genres (genre_id, name_genres) VALUES (?, ?)";
                    jdbcTemplate.update(insertGenreSql, genre.getId(), genre.getName());
                }
            }

            String sqlInsertGenre = "INSERT INTO filmgenres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertGenre, film.getId(), genre.getId());
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновление фильма {}", film.getName());
        final String sqlQuery = "UPDATE films SET " +
                "name = ?, " +
                "description = ?, " +
                "releaseDate = ?, " +
                "duration = ? " + // Removed comma after duration
                "WHERE film_id = ?;";
        int temp = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
        if (temp == 0) {
            throw new NotFoundException("Невозможно обновить фильм с id = " + film.getId()); // Исправлена опечатка в сообщении исключения
        }
        return film;
    }


    @Override
    public void deleteFilm(Long id) {
        String deleteGenreSql = "DELETE FROM filmgenres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenreSql, id);
        String sqlQuery = "delete from films where film_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Удаление фильма c id = {}", id);
    }

    @Override
    public Film getFilmByID(Long id) {
        log.info("Фильм с id = {} ", id);
        String sqlQuery = "select * from films " +
                "left join mpa on films.mpa_id = mpa.mpa_id " +
                "where films.film_id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, FilmRowMapper::mapRow, id);
            final String sqlQueryGenres = "select * from FILMGENRES " +
                    "left join genres " +
                    "on FILMGENRES.genre_id = genres.genre_id " +
                    "where FILMGENRES.film_id = ?;";
            film.setGenres(new HashSet<>(jdbcTemplate.query(sqlQueryGenres, GenresMapper::mapRow, id)));
            return film;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
    }

    @Override
    public List<Film> allFilms() {
        log.info("Список всех фильмов");

        String sqlQuery = "SELECT * FROM films " +
                "left join mpa on films.mpa_id = mpa.mpa_id " +
                "LEFT JOIN filmgenres ON films.film_id = filmgenres.film_id " +
                "LEFT JOIN genres ON filmgenres.genre_id = genres.genre_id " +
                "LEFT JOIN likes ON likes.film_id = films.film_id;";
        return jdbcTemplate.query(sqlQuery, FilmRowMapper::mapRow);
    }


    @Override
    public void takeLike(Long filmId, Long userId) {
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        final String sqlQuery = "insert into likes (film_id,user_id) values (?,?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement(sqlQuery);
            pr.setLong(1, filmId);
            pr.setLong(2, userId);
        jdbcTemplate.update(save, userId, filmId, "LIKE", "ADD", LocalDateTime.now());
            return pr;
        });
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        log.info("Пользователь с id = {} убрал лайк с фильму с id = {}", userId, id);
        final String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        jdbcTemplate.update(save, userId, id, "LIKE", "REMOVE", LocalDateTime.now());
    }

    public List<Film> getPopularFilm(Long limit) {
        log.info("Популярные фильмы ");
        String sqlQuery = "SELECT * " +
                "FROM films " +
                "inner join mpa on films.mpa_id = mpa.mpa_id " +
                "WHERE film_id IN ( " +
                "    SELECT  likes.film_id " +
                "    FROM likes " +
                "    GROUP BY likes.film_id " +
                "    ORDER BY COUNT(likes.user_id) DESC " +
                "limit ?" +
                ");";
        return jdbcTemplate.query(sqlQuery, FilmRowMapper::mapRow, limit);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        String request1 = "SELECT * FROM films " +
                "left join mpa on films.mpa_id = mpa.mpa_id " +
                "WHERE film_id IN (SELECT film_id FROM likes " +
                "WHERE user_id = ? AND (SELECT film_id FROM likes WHERE user_id = ?))";
        String request2 = "select * from filmgenres " +
                "inner join genres on filmgenres.genre_id = genres.genre_id " +
                "where filmgenres.film_id = ?;";
        List<Film> films = jdbcTemplate.query(request1, FilmRowMapper::mapRow, userId, friendId);
        for (Film film : films) {
            film.setGenres(new HashSet<>(jdbcTemplate.query(request2, GenresMapper::mapRow, film.getId())));
        }
        return films;
    }

    public Review postReview(Review review) {
        review.setReviewId(review_id);
        review_id++;
        String request = "INSERT INTO reviews(content, positive, user_id, film_id, useful) " +
                "values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(request, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId(), review.getUseful());
        jdbcTemplate.update(save, review.getUserId(), review.getReviewId(), "REVIEW", "ADD", LocalDateTime.now());
        System.out.println("Добавление комментария");
        return review;
    }

    public Review putReview(Review review) {
        String request = "UPDATE reviews SET " + "content = ?, positive = ?, user_id = ?, film_id = ?, useful = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(request, review.getContent(), review.getIsPositive(), review.getUserId(),
                review.getFilmId(), review.getUseful(), review.getReviewId());
        jdbcTemplate.update(save, review.getUserId(), review.getReviewId(), "REVIEW", "UPDATE",
                LocalDateTime.now());
        System.out.println("Обновление комментария");
        return review;
    }

    public Review getReview(long id) {
        System.out.println("Получение комментария");
        try {
            String request = "SELECT * FROM reviews WHERE id = ?";
            return jdbcTemplate.queryForObject(request, new ReviewRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Комментарий не найден");
        }
    }

    public List<Review> getReviews(long filmId) {
        System.out.println("Получение комментариев");
        String request = "SELECT * FROM reviews WHERE film_id = ?";
        return jdbcTemplate.query(request, new ReviewRowMapper(), filmId);
    }

    public List<Review> delReview(long id) {
        Review review = getReview(id);
        String request = "DELETE FROM reviews WHERE id = ?";
        String request1 = "SELECT * FROM reviews";
        jdbcTemplate.update(request, id);
        jdbcTemplate.update(save, review.getUserId(), review.getReviewId(), "REVIEW", "REMOVE",
                LocalDateTime.now());
        return jdbcTemplate.query(request1, new ReviewRowMapper());
    }

    public Review addReviewLike(long id, long userId) {
        System.out.println("Добавление лайка");
        Review review = getReview(id);
        String check = "DELETE FROM dislike_reviews WHERE user_id = ? AND review_id = ?";
        int result = jdbcTemplate.update(check, id, userId);
        if (result == 0) {
            review.setUseful(review.getUseful() + 1);
        } else {
            review.setUseful(review.getUseful() + 2);
        }
        putReview(review);
        String request = "INSERT INTO like_reviews(user_id, review_id) " + "values(?, ?)";
        jdbcTemplate.update(request, id, userId);
        return review;
    }

    public Review delReviewLike(long id, long userId) {
        System.out.println("Удаление лайка");
        Review review = getReview(id);
        String check = "DELETE FROM like_reviews WHERE user_id = ? AND review_id = ?";
        int result = jdbcTemplate.update(check, id, userId);
        if (result == 1) {
            review.setUseful(review.getUseful() - 1);
        }
        putReview(review);
        String request = "DELETE FROM like_reviews WHERE user_id = ? AND review_id = ?";
        jdbcTemplate.update(request, id, userId);
        return review;
    }

    public Review addReviewDislike(long id, long userId) {
        System.out.println("Добавление дизлайка");
        Review review = getReview(id);
        String check = "DELETE FROM like_reviews WHERE user_id = ? AND review_id = ?";
        int result = jdbcTemplate.update(check, id, userId);
        if (result == 1) {
            review.setUseful(review.getUseful() - 2);
        } else {
            review.setUseful(review.getUseful() - 1);
        }
        putReview(review);
        String request = "INSERT INTO dislike_reviews(user_id, review_id) " + "values(?, ?)";
        jdbcTemplate.update(request, id, userId);
        return review;
    }
}
