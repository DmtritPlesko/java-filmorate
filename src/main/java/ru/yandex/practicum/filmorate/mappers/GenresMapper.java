package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenresMapper implements RowMapper<Genre> {
    public Genre mapRow(ResultSet rs, int mapRow) throws SQLException {
        return new Genre(rs.getLong("genres.genre_id"), rs.getString("genres.name_genres"));
    }
}
