package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;

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
            throw new NotFoundException("user с id=" + id + " не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        if (users.isEmpty()) {
            log.info("Список с пользователями пустой.");
        }
        return users.values();
    }

    @Override
    public User createUser(User user) {
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
            throw new NotFoundException("user " + user.getName() + " не найден.");
        }
        users.put(user.getId(), user);
        log.info("user " + user.getName() + " обновлён.");
        return user;
    }
    
    @Override
    public boolean isExist(int id) {
        return users.get(id) != null;
    }
}
