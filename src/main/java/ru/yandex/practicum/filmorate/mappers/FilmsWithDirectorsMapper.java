package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmsWithDirectorsMapper {
    public static Film filmAndDirectorMapper(ResultSet rs, int rowNum) throws SQLException {
        Film film = FilmRowMapper.mapRow(rs, rowNum);

        Director director = new Director(rs.getLong("film_directors.director_id"));

        film.getDirectors().add(director);

        return film;
    }
}
