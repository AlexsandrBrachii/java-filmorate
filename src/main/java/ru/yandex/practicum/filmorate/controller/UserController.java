package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.event.EventService;

import java.util.Collection;
import java.util.List;

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
        return userService.createUser(user);
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{userId}")
    public String deleteUser(@PathVariable int userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    private void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    private void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/{userId}/recommendations")
    private List<Film> getRecommendations(@PathVariable int userId) {
        return userService.getRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable int id) {
        return eventService.getFeed(id);
    }
}
