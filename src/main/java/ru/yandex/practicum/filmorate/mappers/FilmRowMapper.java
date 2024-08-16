package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name"));
        Set<Long> likes = new LinkedHashSet<>();
        Set<Genre> genres = new LinkedHashSet<>();
        Set<Director> directors = new LinkedHashSet<>();

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getLong("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(mpa)
                .likes(likes)
                .genres(genres)
                .directors(directors)
                .build();

        // Обработка лайков и жанров и режиссеров
        if (Objects.nonNull(rs.getString("user_id"))) {
            likes.add(rs.getLong("user_id"));
        }
        if (Objects.nonNull(rs.getString("genre_id"))) {
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            genres.add(genre);
        }

        if (Objects.nonNull(rs.getString("director_id"))) {
            Director director = new Director(rs.getLong("director_id"),
                    rs.getString("director_name"));
            directors.add(director);
        }

        return film;
    }
}
