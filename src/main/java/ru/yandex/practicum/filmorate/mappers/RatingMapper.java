package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingMapper implements RowMapper<Mpa> {
    @Override
    public Mpa mapRow(ResultSet rs, int mapRow) throws SQLException {
        return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
    }
}
