package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public void addFriend(int idUser, long idFriend) {
        if (idUser < 1 || idFriend < 1) {
            log.info("id пользователей не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id пользователей не может быть отрицательным.");
        }
        Collection<User> usersCopy = userStorage.getAllUsers();

        Optional<User> optionalUser = usersCopy.stream()
                .filter(user -> user.getId().equals(idUser))
                .findFirst();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.getFriends().add(idFriend);
            log.info("friend с id=" + idFriend + " добавлен в друзья user с id=" + idUser);

            Optional<User> optionalFriends = usersCopy.stream()
                    .filter(friend -> friend.getId().equals((int) idFriend))
                    .findFirst();
            if (optionalFriends.isPresent()) {
                User friend = optionalFriends.get();
                friend.getFriends().add((long) idUser);
                log.info("User с id=" + idUser + " добавлен в друзья user с id=" + idFriend);
            } else {
                log.info("friend с id=" + idFriend + " не найден.");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "friend с id=" + idFriend + " не найден.");
            }
        } else {
            log.info("user с id=" + idUser + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user с id=" + idUser + " не найден.");
        }
    }

    public void deleteFriend(int idUser, long idFriend) {
        if (idFriend < 1 || idUser < 1) {
            log.info("id не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id не может быть отрицательным.");
        }
        Collection<User> usersCopy = userStorage.getAllUsers();

        Optional<User> optionalUser = usersCopy.stream()
                .filter(user -> user.getId().equals(idUser))
                .findFirst();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.getFriends().remove(idFriend);
            log.info("friend с id=" + idFriend + " удалён из друзей user с id=." + idUser);

            Optional<User> optionalFriends = usersCopy.stream()
                    .filter(friend -> friend.getId().equals((int) idFriend))
                    .findFirst();
            if (optionalFriends.isPresent()) {
                User friend = optionalFriends.get();
                friend.getFriends().remove((long) idUser);
                log.info("User с id=" + idUser + " удалён из друзей user с id=" + idFriend);
            } else {
                log.info("friend с id=" + idFriend + " не найден.");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "friend с id=" + idFriend + " не найден.");
            }
        } else {
            log.info("user с id=" + idUser + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user с id=" + idUser + " не найден.");
        }

    }

    public List<User> getFriends(int idUser) {
        if (idUser < 1) {
            log.info("id не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id не может быть отрицательным.");
        }
        Collection<User> usersCopy = userStorage.getAllUsers();
        List<User> friends = new ArrayList<>();

        Optional<User> optionalUser = usersCopy.stream()
                .filter(user -> user.getId().equals(idUser))
                .findFirst();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<Long> idFriends = user.getFriends();
            if (idFriends.isEmpty()) {
                log.info("У user c id=" + idUser + " не найдено друзей.");
            } else {
                for (Long idFriend : idFriends) {
                    User friend = userStorage.getUser(Math.toIntExact(idFriend));
                    friends.add(friend);
                }
            }
            return friends;
        } else {
            log.info("user с id=" + idUser + " не найден.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user с id=" + idUser + " не найден.");
        }

    }

    public List<User> haveCommonFriends(int idUser, int idFriend) {
        if (idFriend < 1 || idUser < 1) {
            log.info("id не может быть отрицательным.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id не может быть отрицательным.");
        }
        List<User> friendsUser = getFriends(idUser);
        List<User> friendsFriend = getFriends(idFriend);

        friendsUser.retainAll(friendsFriend);

        if (friendsUser.isEmpty()) {
            log.info("Не найдено общих друзей.");
        }
        return friendsUser;
    }


}