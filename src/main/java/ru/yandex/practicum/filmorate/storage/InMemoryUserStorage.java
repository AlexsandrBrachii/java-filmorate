package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Integer identifier = 1;

    private HashMap<Integer, User> users = new HashMap<>();


    @Override
    public User getUser(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.info("user с id=" + id + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user с id=" + id + " не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        if (!users.isEmpty()) {
            return users.values();
        } else {
            log.info("Список с пользователями пустой.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Список с пользователями пустой.");
        }
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        user.setId(identifier);
        users.put(identifier, user);
        identifier++;
        log.info("user " + user.getName() + " добавлен.");
        return user;
    }

    @Override
    public User updateUser(User user) {
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
