package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {

    User getUser(int id);

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);
}
