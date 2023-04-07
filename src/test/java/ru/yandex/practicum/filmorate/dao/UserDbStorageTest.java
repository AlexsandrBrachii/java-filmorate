package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    @DirtiesContext
    void getUser_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(1, user.getId());
    }

    @Test
    @DirtiesContext
    void getAllUsers_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);
        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest1);

        Collection<User> users = userStorage.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    @DirtiesContext
    void createUser_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(1, user.getId());
    }

    @Test
    @DirtiesContext
    void updateUser_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        User userTest1 = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.updateUser(userTest1);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals("new name", user.getName());
    }

    @Test
    @DirtiesContext
    void addFriend_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest1);

        userStorage.addFriend(userTest.getId(), userTest1.getId());
        List<User> friends = userStorage.getFriends(userTest.getId());

        assertEquals(1, friends.size());
        assertEquals(userTest1, friends.get(0));
    }

    @Test
    @DirtiesContext
    void deleteFriend_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest);

        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userStorage.createUser(userTest1);

        userStorage.addFriend(userTest.getId(), userTest1.getId());
        List<User> friends = userStorage.getFriends(userTest.getId());

        assertEquals(1, friends.size());
        assertEquals(userTest1, friends.get(0));

        userStorage.deleteFriend(userTest.getId(), userTest1.getId());

        List<User> friends1 = userStorage.getFriends(userTest.getId());

        assertTrue(friends1.isEmpty());
    }
}