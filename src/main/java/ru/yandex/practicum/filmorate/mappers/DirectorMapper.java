package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper {
    public static Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("director_id"),
                rs.getString("director_name"));
    }
}
