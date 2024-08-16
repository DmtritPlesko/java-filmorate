package ru.yandex.practicum.filmorate.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.userDb.UserStorageInterface;
import ru.yandex.practicum.filmorate.storage.dao.userDb.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserDbService {
    private final UserStorageInterface userStorage;
    private final FilmDbService filmDbService;
    private final FeedService feedService;

    @Autowired
    public UserDbService(UserDbStorage userStorage, FilmDbService filmDbService, FeedService feedService) {
        this.userStorage = userStorage;
        this.filmDbService = filmDbService;
        this.feedService = feedService;
    }

    public User addUser(User user) {
        validation(user);
        return userStorage.createUser(user);
    }

    public void addNewFriend(Long userId, Long friendId) {
        userStorage.addNewFriend(userId, friendId);
        feedService.create(userId, friendId, "FRIEND", "ADD");
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUser() {
        return userStorage.allUser();
    }

    public Set<User> allFriend(Long userId) {
        return userStorage.allFriend(userId);
    }

    public Set<User> getMutualFriend(Long userId, Long friendId) {
        return userStorage.getMutualFriends(userId, friendId);
    }

    public User updateUser(User user) {
        validation(user);
        return userStorage.update(user);
    }

    public void deleteFriendFromUser(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
        feedService.create(userId, friendId, "FRIEND", "REMOVE");
    }

    /*
    Метод для получения рекомендаций пользователя с максимальным количеством похожих лайков,
     и при этом максимальным количеством новых фильмов среди подходящих пользователей.
     */
    public Collection<Film> getRecommendations(Long userId) {
        log.info("Получить список рекомендаций для пользователя с id = {}", userId);

        Collection<User> users = new HashSet<>(getAllUser());
        users.remove(getUserById(userId));

        Collection<Film> currentUserFilms = getUsersFavouritesFilms(userId);
        long maxSimilar = 0;
        long maxNotSimilar = 0;
        long maxSimilarUserId = -1;
        for (User user : users) {
            Collection<Film> tempFilms = new HashSet<>(currentUserFilms);
            tempFilms.retainAll(getUsersFavouritesFilms(user.getId()));
            long currentSimilar = tempFilms.size();
            tempFilms = new HashSet<>(getUsersFavouritesFilms(user.getId()));
            tempFilms.removeAll(currentUserFilms);
            long currentNotSimilar = tempFilms.size();
            if (currentSimilar > maxSimilar) {
                maxSimilar = currentSimilar;
                maxNotSimilar = currentNotSimilar;
                maxSimilarUserId = user.getId();
            } else if (currentSimilar == maxSimilar && currentNotSimilar > maxNotSimilar && currentSimilar > 0) {
                maxNotSimilar = currentNotSimilar;
                maxSimilarUserId = user.getId();
            }
        }
        if (maxSimilarUserId == -1) {
            return new ArrayList<>();
        }

        Collection<Film> recommendedFilms = getUsersFavouritesFilms(maxSimilarUserId);
        recommendedFilms.removeAll(currentUserFilms);
        return recommendedFilms;
    }

    private Collection<Film> getUsersFavouritesFilms(long userId) {
        Collection<Film> userFavouritesFilms = new HashSet<>();
        for (Film film : filmDbService.getAllFilms()) {
            if (film.getLikes().contains(userId)) {
                userFavouritesFilms.add(film);
            }
        }
        return userFavouritesFilms;
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUser(id);
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
        } else if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

}
