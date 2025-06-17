package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    private Long id = 0L;

    public User addUser(User user) {
        log.info("Добавляем пользователя: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя для отображения пустое — в таком случае будет используем логин.");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("{} Пользователь успешно добавлен.", user);
        return user;
    }

    public User updateUser(User user) {
        log.info("Обновляем пользователя: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя для отображения пустое — в таком случае будет используем логин.");
        }
        users.put(user.getId(), user);
        log.info("{} Пользователь успешно обновлен.", user);
        return user;
    }

    public List<User> getUsers() {
        log.info("Получаем список пользователей, его размер: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
        return user;
    }

}
