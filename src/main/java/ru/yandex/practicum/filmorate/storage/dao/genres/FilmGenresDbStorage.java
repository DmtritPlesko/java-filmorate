package ru.yandex.practicum.filmorate.storage.dao.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.GenresMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmGenresDbStorage implements FilmGenres {
    private final JdbcTemplate jdbcTemplate;
    private final GenresMapper genresMapper;

    @Override
    public Genre getGenresById(Long id) {
        log.info("Пытаемся взять жанр с id = {}", id);

        final String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?;";
        return jdbcTemplate.queryForObject(sqlQuery, genresMapper, id);

    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Береём все жанры");

        final String sqlQuery = "SELECT * FROM genres;";
        return jdbcTemplate.query(sqlQuery, genresMapper);
    }

}
