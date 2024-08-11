package ru.yandex.practicum.filmorate.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.userDb.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorageInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserDbService {
    private UserStorageInterface userStorage;

    @Autowired
    public UserDbService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    //create
    public User addUser(User user) {
        validation(user);
        return userStorage.createUser(user);
    }

    public void addNewFriend(Long userId, Long friendId) {
        userStorage.addNewFriend(userId, friendId);
    }

    //read
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUser() {
        return userStorage.allUser();
    }

    public Set<User> allFriend(Long userId) {
        return userStorage.allFriend(userId);
    }

    public List<User> getMutualFriend(Long userId, Long friendId) {
        return userStorage.getMutualFriends(userId, friendId);
    }

    //update
    public User updateUser(User user) {
        validation(user);
        return userStorage.update(user);
    }

    //delete
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void deleteFriendFromUser(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<Feed> getFeed(Long userId) {
        return userStorage.getFeed(userId);
    }

    private void validation(User user) {
        if (StringUtils.isBlank(user.getEmail())) {
            throw new ValidationException("Электронная почта не может быть пустой");
        } else if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", user.getEmail())) {
            throw new ValidationException("Неверный формат почты");
        } else if (StringUtils.isBlank(user.getLogin())) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

}
