package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;

    @Test
    @DirtiesContext
    void getUser_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest);

        Optional<User> userOptional = Optional.ofNullable(userService.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(1, user.getId());
        assertEquals("awb@mail.ru", user.getEmail());
        assertEquals("awb", user.getLogin());
        assertEquals("Alex", user.getName());
        assertEquals(LocalDate.of(1996, 8, 9), user.getBirthday());
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
        userService.createUser(userTest);
        User userTest1 = User.builder()
                .id(2)
                .email("awbb@mail.ru")
                .login("awbb")
                .name("Alexsandr")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest1);

        Collection<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("awb@mail.ru", userTest.getEmail());
        assertEquals("awb", userTest.getLogin());
        assertEquals("Alex", userTest.getName());
        assertEquals(LocalDate.of(1996, 8, 9), userTest.getBirthday());
        assertEquals("awbb@mail.ru", userTest1.getEmail());
        assertEquals("awbb", userTest1.getLogin());
        assertEquals("Alexsandr", userTest1.getName());
        assertEquals(LocalDate.of(1996, 8, 9), userTest1.getBirthday());
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
        userService.createUser(userTest);

        Optional<User> userOptional = Optional.ofNullable(userService.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(1, user.getId());
        assertEquals("awb@mail.ru", user.getEmail());
        assertEquals("awb", user.getLogin());
        assertEquals("Alex", user.getName());
        assertEquals(LocalDate.of(1996, 8, 9), user.getBirthday());
    }

    @Test
    @DirtiesContext
    void createUser_withEmptyName() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest);

        Optional<User> userOptional = Optional.ofNullable(userService.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(1, user.getId());
        assertEquals("awb@mail.ru", user.getEmail());
        assertEquals("awb", user.getLogin());
        assertEquals("awb", user.getName());
        assertEquals(LocalDate.of(1996, 8, 9), user.getBirthday());
    }

    @Test
    @DirtiesContext
    void createUser_withWrongEmail() {
        User userTest = User.builder()
                .id(1)
                .email("awb.mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(userTest));

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }

    @Test
    @DirtiesContext
    void createUser_withEmptyLogin() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login(" ")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(userTest));

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
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
        userService.createUser(userTest);

        User userTest1 = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.updateUser(userTest1);

        Optional<User> userOptional = Optional.ofNullable(userService.getUser(userTest.getId()));

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals("new name", user.getName());
    }

    @Test
    @DirtiesContext
    void updateUser_withWrongId() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest);

        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(userTest1));

        assertEquals("User с id " + userTest1.getId() + " не найден.", exception.getMessage());
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
        userService.createUser(userTest);

        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest1);

        userService.addFriend(userTest.getId(), userTest1.getId());
        List<User> friends = userService.getFriends(userTest.getId());

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
        userService.createUser(userTest);

        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("new name")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest1);

        userService.addFriend(userTest.getId(), userTest1.getId());
        List<User> friends = userService.getFriends(userTest.getId());

        assertEquals(1, friends.size());
        assertEquals(userTest1, friends.get(0));

        userService.deleteFriend(userTest.getId(), userTest1.getId());

        List<User> friends1 = userService.getFriends(userTest.getId());

        assertTrue(friends1.isEmpty());
    }

    @Test
    @DirtiesContext
    void haveCommonFriends_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest);

        User userTest1 = User.builder()
                .id(2)
                .email("awb@mail.ru")
                .login("awb")
                .name("Bob")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest1);

        User userTest2 = User.builder()
                .id(3)
                .email("awb@mail.ru")
                .login("awb")
                .name("Jack")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest2);

        userService.addFriend(userTest.getId(), userTest2.getId());
        userService.addFriend(userTest1.getId(), userTest2.getId());

        List<User> commonFriend = userService.haveCommonFriends(userTest.getId(), userTest1.getId());

        assertEquals(1, commonFriend.size());
        User jack = commonFriend.get(0);
        assertEquals("Jack", jack.getName());
    }

    @Test
    @DirtiesContext
    void deleteUser_withNormalBehavior() {
        User userTest = User.builder()
                .id(1)
                .email("awb@mail.ru")
                .login("awb")
                .name("Alex")
                .birthday(LocalDate.of(1996, 8, 9)).build();
        userService.createUser(userTest);
        List<User> UsersBeforeDelete = new ArrayList<>(userService.getAllUsers());
        assertEquals(1, UsersBeforeDelete.size());

        userService.deleteUser(1);
        List<User> UsersAfterDelete = new ArrayList<>(userService.getAllUsers());
        assertEquals(0, UsersAfterDelete.size());
    }
}