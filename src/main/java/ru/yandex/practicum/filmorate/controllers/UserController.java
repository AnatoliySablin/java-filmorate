package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    final private Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавляем пользователя: " + user);
        validationUser(user);
        user.setId(++id);
        users.put(user.getId(), user);
        log.info(user + " Пользователь успешно добавлен.");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновляем пользователя: " + user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(user + " Такой пользователь не зарегистрирован");
        }
        validationUser(user);
        users.put(user.getId(), user);
        log.info(user + " Пользователь успешно обновлен.");
        return user;
    }

    @GetMapping
    public List<User> listUsers() {
        log.info("Получаем список пользователей, его размер: " + users.size());
        return new ArrayList<>(users.values());
    }

    private void validationUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя для отображения пустое — в таком случае будет используем логин.");
        }
    }
}
