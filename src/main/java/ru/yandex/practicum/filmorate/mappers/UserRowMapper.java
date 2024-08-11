package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRowMapper {
    public static User mapRow(ResultSet rs, int numRow) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .years(rs.getInt("age"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .password(rs.getString("password"))
                .build();

        return user;
    }
}
