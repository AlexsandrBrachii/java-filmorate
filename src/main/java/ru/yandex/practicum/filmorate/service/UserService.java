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
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser((int) idFriend);
        user.getFriends().add(idFriend);
        log.info("friend с id=" + idFriend + " добавлен в друзья user с id=" + idUser);
        friend.getFriends().add(idUser);
        log.info("User с id=" + idUser + " добавлен в друзья user с id=" + idFriend);
    }

    public void deleteFriend(int idUser, int idFriend) {
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        user.getFriends().remove(idFriend);
        log.info("friend с id=" + idFriend + " удалён из друзей user с id=." + idUser);
        friend.getFriends().remove(idUser);
        log.info("User с id=" + idUser + " удалён из друзей user с id=" + idFriend);
    }

    public List<User> getFriends(int idUser) {
        List<User> friends = new ArrayList<>();
        User user = userStorage.getUser(idUser);
        Set<Integer> idFriends = user.getFriends();
        if (idFriends.isEmpty()) {
            log.info("У user c id=" + idUser + " не найдено друзей.");
        } else {
            for (Integer idFriend : idFriends) {
                User friend = userStorage.getUser(Math.toIntExact(idFriend));
                friends.add(friend);
            }
        }
        return friends;
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