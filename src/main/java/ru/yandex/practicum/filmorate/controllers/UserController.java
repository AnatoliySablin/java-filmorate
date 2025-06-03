package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавляем пользователя: " + user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновляем пользователя: " + user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendById(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление пользователя по id: " + friendId + " в друзья к пользователю по id: " + id);
        userService.addFriendById(id, friendId);
    }

    @GetMapping
    public List<User> listUsers() {
        log.info("Получаем список пользователей, его размер: " + userService.listUsers().size());
        return userService.listUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получаем пользователя по id: " + id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getListFriends(@PathVariable Long id) {
        log.info("Получаем список друзей пользователя по id: " + id);
        return userService.getListFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Set<User> getListFriendsSharedWithAnotherUser(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получаем список друзей пользователя по id: " + id + " общих с другим пользователем по id: " + otherId);
        return userService.getListFriendsSharedWithAnotherUser(id, otherId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriendById(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаление пользователя по id: " + friendId + " из друзей пользователя по id: " + id);
        userService.deleteFriendById(id, friendId);
    }
}
