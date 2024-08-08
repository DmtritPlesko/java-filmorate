package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
        Set<Long> likes = new LinkedHashSet<>();
        Set<Genre> genres = new LinkedHashSet<>();

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getLong("duration"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .mpa(mpa)
                .likes(likes)
                .genres(genres)
                .build();

        // Обработка лайков и жанров
        if (rs.getString("user_id") != null) {
            likes.add(rs.getLong("user_id"));
        }
        if (rs.getString("genre_id") != null) {
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            genres.add(genre);
        }

        return film;
    }
}