package ru.yandex.practicum.filmorate.storage.dao.filmDb;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;
import ru.yandex.practicum.filmorate.storage.dao.userDb.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Setter
@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorageInterface {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final NamedParameterJdbcOperations jdbc;
    private final UserDbStorage userDbStorage;

    @Override
    public Film addNewFilm(Film film) {
        log.info("Добавление нового фильма в БД");

        final String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Number number = keyHolder.getKey();
        film.setId(number.longValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                boolean exists = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM genres WHERE genre_id = ?", Integer.class, genre.getId()) > 0;

                if (!exists) {
                    String insertGenreSql = "INSERT INTO genres (genre_id, genre_name) VALUES (?, ?)";
                    jdbcTemplate.update(insertGenreSql, genre.getId(), genre.getName());
                }
            }

            String sqlInsertGenre = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertGenre, film.getId(), genre.getId());
            }
        }

        if (film.getDirectors() != null) {
            String sqlInsertFilmDirectors = "INSERT INTO film_directors (film_id,director_id) VALUES (?,?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sqlInsertFilmDirectors, film.getId(), director.getId());
            }
        }
        return getFilmByID(film.getId());
    }

    @Override
    public Film update(Film film) {
        validFilm(film.getId());
        log.info("Обновление фильма {}", film.getName());
        String sql = "UPDATE films SET name = :name, " +
                "description = :description, " +
                "release_date = :release_date, " +
                "duration = :duration, " +
                "mpa_id = :mpa_id " +
                "WHERE film_id = :film_id; ";
        Map<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpa_id", film.getMpa().getId());
        params.put("film_id", film.getId());
        jdbc.update(sql, params);
        addGenres(film.getId(), film.getGenres());
        addDirectors(film.getId(), film.getDirectors());
        return getFilmByID(film.getId());
    }

    private void addGenres(final Long filmId, final Set<Genre> genres) {
        Map<String, Object>[] batch = new HashMap[genres.size()];
        int count = 0;
        for (Genre genre : genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            batch[count++] = map;
        }
        String sqlDelete = "DELETE FROM film_genres WHERE film_id = :film_id; ";
        jdbc.update(sqlDelete, Map.of("film_id", filmId));
        String sqlInsert = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id); ";
        // jdbc.batchUpdate(sqlDelete, batch);
        jdbc.batchUpdate(sqlInsert, batch);
    }

    private void addDirectors(final Long filmId, final Set<Director> directors) {
        Map<String, Object>[] batch = new HashMap[directors.size()];
        int count = 0;
        for (Director director : directors) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("director_id", director.getId());
            batch[count++] = map;
        }
        String sqlDelete = "DELETE FROM film_directors WHERE film_id = :film_id; ";
        jdbc.update(sqlDelete, Map.of("film_id", filmId));
        String sqlInsert = "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id); ";
        // jdbc.batchUpdate(sqlDelete, batch);
        jdbc.batchUpdate(sqlInsert, batch);
    }

    public Film getFilmByID(Long id) {
        validFilm(id);
        log.info("Фильм с id = {} ", id);
        String sqlQuery = "SELECT f.*, l.user_id, fg.genre_id, g.genre_name, m.mpa_name, " +
                "d.director_id, dir.director_name " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
                "LEFT JOIN directors dir on dir.director_id = d.director_id " +
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
                // Добавляем лайки и жанры и режиссеров
                if (rs.getLong("user_id") != 0) {
                    film.getLikes().add(rs.getLong("user_id"));
                }
                if (rs.getLong("genre_id") != 0) {
                    Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                    film.getGenres().add(genre);
                    film.setGenres(film.getGenres().stream()
                            .sorted(Comparator.comparing(Genre::getId))
                            .collect(Collectors.toCollection(LinkedHashSet::new)));
                }

                if (rs.getLong("director_id") != 0) {
                    Director director = new Director(
                            rs.getLong("director_id"),
                            rs.getString("director_name"));
                    film.getDirectors().add(director);

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

        String sqlQuery = "SELECT f.*, l.user_id, fg.genre_id, g.genre_name, m.mpa_name," +
                "d.director_id, dir.director_name FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
                "LEFT JOIN directors dir on dir.director_id = d.director_id";

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
                film.setGenres(film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            if (rs.getLong("director_id") != 0) {
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("director_name"));
                film.getDirectors().add(director);

            }
        });

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public void deleteFilmByID(Long id) {
        validFilm(id);
        log.info("Удаление фильма с id = {}", id);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
    }

    @Override
    public void takeLike(Long filmId, Long userId) {
        validFilm(filmId);
        userDbStorage.validUser(userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        final String sqlQuery = "MERGE INTO likes (film_id,user_id) VALUES (?,?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement(sqlQuery);
            pr.setLong(1, filmId);
            pr.setLong(2, userId);
            return pr;
        });
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        validFilm(id);
        userDbStorage.validUser(userId);
        log.info("Пользователь с id = {} убрал лайк с фильму с id = {}", userId, id);
        final String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public List<Film> getMostPopular(Long count, Long genreId, Integer year) {
        log.info("Популярные фильмы");
        final String sqlQuery = "SELECT f.*, m.mpa_name, l.user_id, fg.genre_id, g.genre_name, " +
                "d.director_id, dir.director_name, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
                "LEFT JOIN directors dir on dir.director_id = d.director_id " +
                "%s " +
                "GROUP BY f.film_id, m.mpa_name, l.user_id, fg.genre_id, g.genre_name, " +
                "d.director_id, dir.director_name " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        String condition = "";
        List<Object> params = new ArrayList<>();
        if (genreId != null) {
            condition = condition + "WHERE fg.genre_id = ?";
            params.add(genreId);
        }
        if (genreId != null && year != null) {
            condition = condition + "AND YEAR(f.release_date) = ?";
            params.add(year);
        }
        if (genreId == null && year != null) {
            condition = condition + "WHERE YEAR(f.release_date) = ?";
            params.add(year);
        }

        params.add(count);
        String sql = String.format(sqlQuery, condition);

        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
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
                film.setGenres(film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            if (rs.getLong("director_id") != 0) {
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("director_name"));
                film.getDirectors().add(director);

            }
        }, params.toArray());

        return new ArrayList<>
                (allFilms().stream()
                        .filter((film -> filmMap.containsKey(film.getId())))
                        .toList());
    }

    @Override
    public List<Film> getFilmBySort(Long id, List<String> sortBy) {
        String sqlQuery = "SELECT f.*, m.mpa_name, l.user_id, fg.genre_id, g.GENRE_NAME AS genre_name, d.director_id," +
                "dir.director_name, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
                "LEFT JOIN directors dir on dir.director_id = d.director_id " +
                "WHERE dir.director_id = ? " +
                "GROUP BY f.film_id, m.mpa_name, l.user_id, fg.genre_id, g.GENRE_NAME, d.director_id, dir.director_name ";

        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbcTemplate.query(sqlQuery, rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = filmRowMapper.mapRow(rs, rs.getRow());
                filmMap.put(filmId, film);
            }

            if (rs.getLong("user_id") != 0) {
                film.getLikes().add(rs.getLong("user_id"));
            }
            if (rs.getLong("genre_id") != 0) {
                Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                film.getGenres().add(genre);
                film.setGenres(film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            if (rs.getLong("director_id") != 0) {
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("director_name"));
                film.getDirectors().add(director);

            }
        }, id);
        List<Film> films;
        if (sortBy.contains("likes")) {

            // Преобразуем в список и сортируем по количеству лайков
            films = new ArrayList<>(filmMap.values());
            films.sort(Comparator.comparingLong(f -> f.getLikes().size()));
        } else {
            films = new ArrayList<>(filmMap.values());
            films.sort(Comparator.comparingLong(f -> f.getReleaseDate().getYear()));
        }
        return films;
    }

    private List<Film> sortByYears(Long id) {
        String sqlQuery = "SELECT f.*, l.user_id, fg.genre_id, g.genre_name, m.mpa_name," +
                "d.director_id, dir.director_name FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
                "LEFT JOIN directors dir on dir.director_id = d.director_id " +
                "WHERE d.director_id = ? " +
                "ORDER BY EXTRACT(YEAR FROM CAST(release_date AS DATE))";

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
                film.setGenres(film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            if (rs.getLong("director_id") != 0) {
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("director_name"));
                film.getDirectors().add(director);

            }
        }, id);
        return new ArrayList<>(filmMap.values());
    }

    private List<Film> sortByLikes(Long id) {
        String sqlQuery = "SELECT f.*, l.user_id, fg.genre_id, g.genre_name, m.mpa_name," +
                "d.director_id, dir.director_name FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
                "LEFT JOIN directors dir on dir.director_id = d.director_id " +
                "WHERE f.film_id IN ( SELECT likes.film_id AS gg " +
                "FROM likes GROUP BY (gg) ORDER BY (count(likes.user_id)) Desc ) AND d.director_id = ?";

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
            if (rs.getLong("director_id") != 0) {
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("director_name"));
                film.getDirectors().add(director);

            }
        }, id);
        return new ArrayList<>(filmMap.values());
    }

    //общие фильмы
    public List<Film> getCommonFilms(Long userId, Long friendId) {
//        String request1 = "SELECT f.*, l.user_id, fg.genre_id, g.genre_name, m.mpa_name, " +
//                "d.director_id, dir.director_name " +
//                "FROM films f " +
//                "LEFT JOIN likes l ON f.film_id = l.film_id " +
//                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
//                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
//                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
//                "LEFT JOIN film_directors d on f.film_id = d.film_id " +
//                "LEFT JOIN directors dir on dir.director_id = d.director_id " +
//                "WHERE f.film_id IN (SELECT film_id FROM likes " +
//                "WHERE user_id = ? AND (SELECT film_id FROM likes WHERE user_id = ?))" +
//                "LIMIT 1";

        String sql =  "SELECT f.*, m.mpa_name, " +
                "fg.genre_id, g.genre_name, " +
                "fd.director_id, d.director_name, " +
                "COUNT( l.user_id) AS like_count " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE f.film_id IN (SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = :user_id AND film_id IN (SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = :friend_id))" +
                "GROUP BY f.film_id, fg.genre_id, fd.director_id " +
                "ORDER BY like_count DESC; ";

        Map<Long, Film> films = jdbc.query(sql, Map.of("user_id", userId, "friend_id", friendId),
                rs -> {
                    Map<Long, Film> films1 = new LinkedHashMap<>();
                    Set<Genre> genres = new LinkedHashSet<>();
                    Set<Director> directors= new LinkedHashSet<>();
                    while (rs.next()) {
                            if (rs.getLong("genre_id") != 0) {
                                genres.add(new Genre(rs.getLong("genre_id"),
                                                rs.getString("genre_name")));
                            }
                            if (rs.getLong("director_id") != 0) {
                                directors.add(new Director(rs.getLong("director_id"),
                                                rs.getString("director_name")));
                            }
                                Long filmId = rs.getLong("film_id");
                                Film film = new Film();
                                film.setName(rs.getString("name"));
                                film.setDescription(rs.getString("description"));
                                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                                film.setDuration( rs.getLong("duration"));
                                film.setId( rs.getLong("film_id"));
                                film.setMpa(new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
                                film.setDirectors(directors);
                                film.setGenres(genres);
                                films1.put(filmId, film);
                        }
                    return films1;
                });
        assert films != null;
        return films.values().stream().toList();
    }

    @Override
    public List<Film> search(String query, String by) {
        String select = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.mpa_id, m.mpa_name, " +
                "g.genre_id, g.genre_name, " +
                "d.director_id, d.director_name, " +
                "COUNT(DISTINCT l.user_id) AS like_count " +
                "FROM films AS f  " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id  " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id  " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id  " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id  " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id  " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id  ";

        String group = "GROUP BY f.film_id, g.genre_id, d.director_id " +
                "ORDER BY like_count DESC " ;
        String sql;
        String param = "%" + query.toLowerCase() + "%";

        if (by.equals("director")) {
            log.info("Поиск фильма по режиссёру");
            sql = select + "WHERE LOWER(d.director_name) LIKE :param " + group;
        } else if (by.equals("title")) {
            log.info("Поиск фильма по названию");
            sql = select + "WHERE LOWER(f.name) LIKE :param " + group;
        } else {
            log.info("Поиск фильма по названию и по режиссёру");
            sql = select + "WHERE LOWER(f.name) LIKE :param OR LOWER(d.director_name) LIKE :param " + group;
        }

        Map<Long, Film> films = jdbc.query(sql, Map.of("param", param),
                rs -> {
                    Map<Long, Film> films1 = new LinkedHashMap<>();
                    Set<Genre> genres = new LinkedHashSet<>();
                    Set<Director> directors= new LinkedHashSet<>();
                    while (rs.next()) {
                        if (rs.getLong("genre_id") != 0) {
                            genres.add(new Genre(rs.getLong("genre_id"),
                                    rs.getString("genre_name")));
                            genres.stream()
                                    .sorted(Comparator.comparing(Genre::getId))
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                        }
                        if (rs.getLong("director_id") != 0) {
                            directors.add(new Director(rs.getLong("director_id"),
                                    rs.getString("director_name")));
                        }
                        Long filmId = rs.getLong("film_id");
                        Film film = new Film();
                        film.setName(rs.getString("name"));
                        film.setDescription(rs.getString("description"));
                        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                        film.setDuration( rs.getLong("duration"));
                        film.setId( rs.getLong("film_id"));
                        film.setMpa(new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
                        film.setDirectors(directors);
                        film.setGenres(genres);
                        films1.put(filmId, film);
                    }
                    return films1;
                });
        assert films != null;
        return new ArrayList<>
                (allFilms().stream()
                        .filter((film -> films.containsKey(film.getId())))
                        .toList());
    }

    public void validFilm(long id) {
        List<Long> ids = jdbcTemplate
                .query("SELECT film_id FROM films; ", (rs, rowNum) -> rs.getLong("film_id"));
        System.out.println(ids);
        if (!ids.contains(id)) {
            log.error("Фильма с id = {} нет.", id);
            throw new NotFoundException("Фильма с id = {} нет." + id);
        }
    }
}
