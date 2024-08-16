package ru.yandex.practicum.filmorate.storage.dao.userDb;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorageInterface {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public User createUser(User user) {
        log.info("Добавление нового пользователя в бд");

        final String sqlQuery = "INSERT INTO users (login,name,age,email,password,birthday) " +
                "VALUES (?,?,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            pr.setString(1, user.getLogin());
            pr.setString(2, user.getName());
            pr.setInt(3, user.getYears());
            pr.setString(4, user.getEmail());
            pr.setString(5, user.getPassword());
            pr.setDate(6, Date.valueOf(user.getBirthday()));
            return pr;
        }, keyHolder);

        Number generatedKey = keyHolder.getKey();
        user.setId(generatedKey.longValue());
        return user;
    }


    @Override
    public User update(User user) {
        validUser(user.getId());
        log.info("Обновление пользователя с ID {}", user.getId());

        final String sqlQuery = "UPDATE users SET name = ?, age = ?, email = ?, login = ?," +
                " password = ?, birthday = ? WHERE user_id = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getYears(),
                user.getEmail(),
                user.getLogin(),
                user.getPassword(),
                user.getBirthday(),
                user.getId());

        if (rowsAffected == 0) {
            log.warn("Пользователь с ID {} не найден или данные не были обновлены", user.getId());
            throw new NotFoundException("Невозможно обновить пользователя с id =" + user.getId());
        }
        return user;
    }

    @Override
    public List<User> allUser() {
        log.info("Берём всех пользователей");

        final String sqlQuery = "SELECT * FROM users;";
        return jdbcTemplate.query(sqlQuery, userRowMapper);
    }

    @Override
    public void addNewFriend(Long userId, Long friendId) {
        validUser(userId);
        validUser(friendId);
        log.info("Добавление нового друга");

        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)", +
                userId, friendId, "unconfirmed");
    }

    @Override
    public void deleteUser(Long id) {
        validUser(id);
        log.info("Удаление пользователся с id = {}", id);

        final String sqlQuery = "DELETE FROM users WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        validUser(userId);
        validUser(friendId);
        log.info("удаление друга");

        final String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("пользователь с id = {} удалил друга с id = {}", userId, friendId);
    }

    @Override
    public User getUserById(Long id) {
        validUser(id);
        log.info("Запрос на получение пользователя с id = {}", id);

        final String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, userRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
    }

    @Override
    public Set<User> allFriend(Long userId) {
        validUser(userId);
        log.info("все друзья пользователя с id = {}", userId);

        final String sqlQuery = "SELECT * FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, userRowMapper, userId));
    }

    @Override
    public Set<User> getMutualFriends(Long userId, Long friendId) {
        validUser(userId);
        validUser(friendId);
        log.info("Поиск общих друзей");

        final String sqlQuery = "SELECT DISTINCT u.* FROM users u JOIN friends f "
                + "ON u.user_id = f.friend_id WHERE f.user_id = ? AND u.user_id "
                + "IN (SELECT f.friend_id FROM friends f WHERE f.user_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                userRowMapper,
                userId,
                friendId));
    }

    public List<Feed> getFeed(Long userId) {
        validUser(userId);
        log.info("Запрос на получение ленты событий пользователя с id = {}", userId);

        final String request = "SELECT * FROM feeds WHERE user_id = ?";
        return jdbcTemplate.query(request, FeedRowMapper::mapRow, userId);
    }

    public void validUser(long id) {
        final List<Long> ids = jdbcTemplate
                .query("SELECT user_id FROM users; ", (rs, rowNum) -> rs.getLong("user_id"));
        if (!ids.contains(id)) {
            log.error("Пользователя с id = {} нет.", id);
            throw new NotFoundException("Пользователя с id = {} нет." + id);
        }
    }
}
