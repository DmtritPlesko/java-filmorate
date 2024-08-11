package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorageInterface {

    User createUser(User user);

    User update(User user);

    List<User> allUser();

    void addNewFriend(Long userId, Long friendId);

    public void deleteUser(Long id);

    void deleteFriend(Long userId, Long friendId);

    User getUserById(Long id);

    Set<User> allFriend(Long userId);

    List<User> getMutualFriends(Long userId, Long friendId);

    List<Feed> getFeed(Long userId);
}
