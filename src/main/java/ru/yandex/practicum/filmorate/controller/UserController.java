package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;



@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }



    @GetMapping
    private Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    private User getUser(@PathVariable int id) {
        return userStorage.getUser(id);
    }

    @GetMapping(value = "/{id}/friends")
    private List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{friendId}")
    private List<User> haveCommonFriends(@PathVariable int id, @PathVariable int friendId) {
        return userService.haveCommonFriends(id, friendId);
    }

    @PostMapping
    private User createUser(@RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    private void addFriend(@PathVariable int id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    private void deleteFriend(@PathVariable int id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
    }
}

