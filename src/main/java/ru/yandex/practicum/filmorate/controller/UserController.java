package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {


    private final UserService userService;




    @GetMapping
    private Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    private User getUser(@PathVariable int id) {
        return userService.getUser(id);
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
        validateUser(user);
        return userService.createUser(user);
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        validateUser(user);
        return userService.updateUser(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    private void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    private void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }


}

