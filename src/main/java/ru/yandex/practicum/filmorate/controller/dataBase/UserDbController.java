package ru.yandex.practicum.filmorate.controller.dataBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserDbController {
    private final UserDbService userDbService;

    @Autowired
    public UserDbController(UserDbService userDbService) {
        this.userDbService = userDbService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userDbService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUser() {
        return userDbService.getAllUser();
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
        return userDbService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userDbService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendsId}")
    public void addFriend(@PathVariable("id") Long userId,
                          @PathVariable("friendsId") Long friendsId) {
        userDbService.addNewFriend(userId, friendsId);
    }

    @DeleteMapping("/{id}/friends/{friendsId}")
    public void deleteFriend(@PathVariable("id") Long userId,
                             @PathVariable("friendsId") Long friendsId) {
        userDbService.deleteFriendFromUser(userId, friendsId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getAllFriend(@PathVariable("id") Long userId) {
        return userDbService.allFriend(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getMutualFriend(@PathVariable("id") Long userId,
                                     @PathVariable("otherId") Long otherId) {
        return userDbService.getMutualFriend(userId, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable("id") Long userId) {
        return userDbService.getRecommendations(userId);
    }

    @GetMapping("/{userId}/feed")
    public List<Feed> getFeed(@PathVariable("userId") Long userId) {
        return userDbService.getFeed(userId);
    }
}
