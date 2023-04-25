package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorageDb {

    User getUser(int id);

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void addFriend(int idUser, int idFriend);

    void deleteFriend(int idUser, int idFriend);

    List<User> getFriends(int idUser);

    List<Integer> getRecommendations(int userId);
}
