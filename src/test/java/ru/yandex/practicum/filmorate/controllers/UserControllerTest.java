package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    void start() {
        user = User.builder()
                .name("name")
                .email("y@ya.ru")
                .login("login")
                .birthday(LocalDate.of(1992, 12, 23))
                .build();
        userController = new UserController();
    }

    @Test
    void getlistUsers() {
        userController.addUser(user);

        assertEquals(1, userController.listUsers().size(), "Количество пользователей не соответствует!");
        assertNotNull(userController.listUsers(), "Список пользователей пустой!");
    }
}