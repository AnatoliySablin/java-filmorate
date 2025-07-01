package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

    private final UserDao userDao;

    public void addFriendById(Long id, Long friendId) {
        userDao.addFriendById(id, friendId);
        log.info(userDao.getUserById(id) + " теперь дружит с " + userDao.getUserById(friendId));
    }

    public void deleteFriendById(Long id, Long friendId) {
        userDao.removeFriendById(id, friendId);
    }

    public List<User> getListFriends(Long id) {
        // являющихся его друзьями.
        return userDao.getListFriends(id);
    }


    public List<User> getListFriendsSharedWithAnotherUser(Long id, Long otherId) {
        return userDao.getCommonFriends(id, otherId);
    }

    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public User addUser(User user) {
        validationFilm(user);
        return userDao.addUser(user);
    }

    public User updateUser(User user) {
        validationFilm(user);
        return userDao.updateUser(user);
    }

    public List<User> listUsers() {
        return userDao.listUsers();
    }

    private User validationFilm(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info(user.getBirthday() + " Ошибка! Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя для отображения пустое — в таком случае будет используем логин.");
        }
        if (user.getLogin().trim().contains(" ")) {
            log.info(user.getLogin() + " Ошибка! Логин не может быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        return user;
    }

}
