package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StatusMapper implements RowMapper<Status> {
    @Override
    public Status mapRow(ResultSet rs, int rowMap) throws SQLException {
        return new Status(rs.getLong("id"),
                rs.getString("status_name"));
    }
}
