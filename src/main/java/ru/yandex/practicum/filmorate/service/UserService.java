package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(int idUser, int idFriend) {
        userStorage.getUser(idUser);
        userStorage.getUser(idFriend);
        userStorage.addFriend(idUser, idFriend);
    }

    public void deleteFriend(int idUser, int idFriend) {
        userStorage.deleteFriend(idUser, idFriend);
    }

    public List<User> getFriends(int idUser) {
        return userStorage.getFriends(idUser);
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
}