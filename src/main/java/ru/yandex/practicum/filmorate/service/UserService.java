package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserStorageDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorageDb userStorageDb;
    private final FilmDbStorage filmDbStorage;

    public Collection<User> getAllUsers() {
        return userStorageDb.getAllUsers();
    }

    public User getUser(int id) {
        User user = userStorageDb.getUser(id);
        if (user == null) {
            throw new NotFoundException("User с id " + id + " не найден.");
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorageDb.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        getUser(user.getId());
        return userStorageDb.updateUser(user);
    }

    public void addFriend(int idUser, int idFriend) {
        getUser(idUser);
        getUser(idFriend);
        userStorageDb.addFriend(idUser, idFriend);
    }

    public void deleteFriend(int idUser, int idFriend) {
        getUser(idUser);
        getUser(idFriend);
        userStorageDb.deleteFriend(idUser, idFriend);
    }

    public List<User> getFriends(int idUser) {
        return userStorageDb.getFriends(idUser);
    }

    public List<User> haveCommonFriends(int idUser, int idFriend) {
        List<User> friendsUser = getFriends(idUser);
        List<User> friendsFriend = getFriends(idFriend);
        friendsUser.retainAll(friendsFriend);
        if (friendsUser.isEmpty()) {
            log.info("Не найдено общих друзей.");
        }
        return friendsUser;
    }

    public List<Film> getRecommendations(int id) {
        return filmDbStorage.getRecommendations(userStorageDb.getRecommendations(id));
    }

    public String deleteUser(int userId) {
        return userStorageDb.deleteUser(userId);
    }
}
