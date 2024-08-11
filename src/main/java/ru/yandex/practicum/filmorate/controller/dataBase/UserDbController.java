package ru.yandex.practicum.filmorate.controller.dataBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserDbController {
    private UserDbService userDbService;

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

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userDbService.deleteUser(id);
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
    public List<User> getMutualFriend(@PathVariable("id") Long userId,
                                      @PathVariable("otherId") Long friendId) {
        return userDbService.getMutualFriend(userId, friendId);
    }

    @GetMapping("/{userId}/feed")
    public List<Feed> getFeed(@PathVariable("userId") Long userId) {
        return userDbService.getFeed(userId);
    }
}
