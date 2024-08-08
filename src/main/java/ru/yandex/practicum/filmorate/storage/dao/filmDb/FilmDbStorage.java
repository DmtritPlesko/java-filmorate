package ru.yandex.practicum.filmorate.storage.dao.filmDb;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Setter
@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorageInterface {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

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
    public Film getFilmByID(Long id) {
        log.info("Фильм с id = {} ", id);
        String sqlQuery = "SELECT f.*, l.user_id, fg.genre_id, g.name_genres AS genre_name, m.mpa_name FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN filmgenres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?;";

        try {
            Map<Long, Film> filmMap = new HashMap<>();

            jdbcTemplate.query(sqlQuery, rs -> {
                Long filmId = rs.getLong("film_id");
                Film film = filmMap.get(filmId);
                if (film == null) {
                    film = filmRowMapper.mapRow(rs, rs.getRow());
                    filmMap.put(filmId, film);
                }
                // Добавляем лайки и жанры
                if (rs.getLong("user_id") != 0) {
                    film.getLikes().add(rs.getLong("user_id"));
                }
                if (rs.getLong("genre_id") != 0) {
                    Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
            }, id);

            if (filmMap.isEmpty()) {
                throw new NotFoundException("Фильм с id=" + id + " не найден");
            }

            return filmMap.values().iterator().next();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
    }

    @Override
    public List<Film> allFilms() {
        log.debug("Список всех фильмов");

        String sqlQuery = "SELECT f.*, l.user_id, fg.genre_id, g.name_genres AS genre_name, m.mpa_name FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN filmgenres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";

        Map<Long, Film> filmMap = new HashMap<>();

        jdbcTemplate.query(sqlQuery, rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = filmRowMapper.mapRow(rs, rs.getRow());
                filmMap.put(filmId, film);
            }
            // Добавляем лайки и жанры
            if (rs.getLong("user_id") != 0) {
                film.getLikes().add(rs.getLong("user_id"));
            }
            if (rs.getLong("genre_id") != 0) {
                Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        });

        return new ArrayList<>(filmMap.values());
    }


    @Override
    public void takeLike(Long filmId, Long userId) {
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        final String sqlQuery = "INSERT INTO likes (film_id,user_id) VALUES (?,?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement(sqlQuery);
            pr.setLong(1, filmId);
            pr.setLong(2, userId);
            return pr;
        });
    }


    @Override
    public void deleteLike(Long id, Long userId) {
        log.info("Пользователь с id = {} убрал лайк с фильму с id = {}", userId, id);
        final String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public List<Film> getPopularFilm(Long limit) {
        log.info("Популярные фильмы");

        String sqlQuery = "SELECT f.*, m.mpa_name, l.user_id, fg.genre_id, g.name_genres AS genre_name, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN filmgenres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "GROUP BY f.film_id, m.mpa_name, l.user_id, fg.genre_id, g.name_genres " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbcTemplate.query(sqlQuery, rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = filmRowMapper.mapRow(rs, rs.getRow());
                filmMap.put(filmId, film);
            }
            // Добавляем лайки и жанры
            if (rs.getLong("user_id") != 0) {
                film.getLikes().add(rs.getLong("user_id"));
            }
            if (rs.getLong("genre_id") != 0) {
                Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        }, limit);

        // Преобразуем в список и сортируем по количеству лайков
        List<Film> films = new ArrayList<>(filmMap.values());
        films.sort((f1, f2) -> Long.compare(f2.getLikes().size(), f1.getLikes().size()));

        return films;
    }

}
