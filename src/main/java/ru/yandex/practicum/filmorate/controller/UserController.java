package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {


    private Integer identifier = 1;
    private HashMap<Integer, User> users = new HashMap<>();


    @GetMapping
    private Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    private User createUser(@RequestBody User user) {
        validateUser(user);
        user.setId(identifier);
        users.put(identifier, user);
        identifier++;
        log.info("user " + user.getName() + " добавлен.");
        return user;
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.info("user " + user.getName() + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user " + user.getName() + " не найден.");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("user " + user.getName() + " обновлён.");
        return user;
    }
}