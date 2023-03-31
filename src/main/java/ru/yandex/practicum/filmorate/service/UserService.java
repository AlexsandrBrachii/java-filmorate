package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;




    public Collection<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User getUser(int id) {
        return userDbStorage.getUser(id);
    }

    public User createUser(User user) {
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userDbStorage.updateUser(user);
    }

    public void addFriend(int idUser, int idFriend) {
        userDbStorage.addFriend(idUser, idFriend);
    }

    public void deleteFriend(int idUser, int idFriend) {
        userDbStorage.deleteFriend(idUser, idFriend);
    }

    public List<User> getFriends(int idUser) {
        return userDbStorage.getFriends(idUser);
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