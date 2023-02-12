package ru.yandex.practicum.filmorate.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;


import static ru.yandex.practicum.filmorate.controller.UserValidator.validateUser;

@RestController
@RequestMapping("/users")
public class UserController {


    private Integer identifier = 1;
    private HashMap<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);



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
        log.info("user добавлен.");
        return user;
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            validateUser(user);
            users.put(user.getId(), user);
            log.info("user обновлён.");
            return user;
        } else {
            log.info("user не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }


}
