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
import ru.yandex.practicum.filmorate.storage.UserStorageInterface;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorageInterface {
    private final JdbcTemplate jdbcTemplate;
    private final String save = "INSERT INTO feeds (user_id, entity_id, event_type, operation, time_stamp) " +
            "values (?, ?, ?, ?, ?)";

    @Override
    public User createUser(User user) {
        log.info("Добавление нового пользователя в бд");
        String sqlQuery = "insert into users (login,name,age,email,password,birthday) " +
                "values (?,?,?,?,?,?);";

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
        log.info("Обновление пользователя с ID {}", user.getId());
        final String sqlQuery = "UPDATE users SET name = ?, age = ?, email = ?, login = ?, password = ?, birthday = ? WHERE user_id = ?";

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
        String sqlQuery = "select * from users;";
        return jdbcTemplate.query(sqlQuery, UserRowMapper::mapRow);
    }

    @Override
    public void addNewFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        log.info("Добавление нового друга");
        final String sqlQueryInsert = "insert into friends (user_id, friend_id,status) values (?,?,?);";
        jdbcTemplate.update(sqlQueryInsert, friendId, userId, "unconfirmed");
        jdbcTemplate.update(save, userId, friendId, "FRIEND", "ADD", LocalDateTime.now());
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        log.info("Удаление пользователся с id = {}", id);
        String sqlQuery = "delete from users where user_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("удаление пользователя");
        getUserById(userId);
        getUserById(friendId);
        log.info("пользователь с id = {} удалил друга с id = {}", userId, friendId);
        final String sqlQuery = "delete friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, friendId, userId);
        jdbcTemplate.update(save, userId, friendId, "FRIEND", "REMOVE", LocalDateTime.now());
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "select * from users where user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, UserRowMapper::mapRow);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
    }

    @Override
    public Set<User> allFriend(Long userId) {
        getUserById(userId);
        log.info("все друзья пользователя с id = " + userId);
        final String sqlQuery = "select * from users " +
                "inner join friends on users.user_id = friends.user_id where friend_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, UserRowMapper::mapRow, userId));
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        log.info("Поиск общих друзей");
        final String sqlQuery = "select distinct u.* from users u " +
                "inner join friends f1 on u.user_id = f1.friend_id " +
                "inner join friends f2 on u.user_id = f2.friend_id " +
                "where f1.user_id = ? and f2.user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserRowMapper::mapRow, friendId, userId);
    }

    public List<Feed> getFeed(Long userId) {
        String request = "SELECT * FROM feeds WHERE user_id = ?";
        return jdbcTemplate.query(request, FeedRowMapper::mapRow, userId);
    }

}
