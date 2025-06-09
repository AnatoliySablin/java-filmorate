package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {
    private final UserStorage userStorage;

    public void addFriendById(Long id, Long friendId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);
        user1.addFriend(friendId);
        user2.addFriend(id);
        log.info("У " + user1 + " теперь в друзьях: " + user1.getFriends());
        log.info("У " + user2 + " теперь в друзьях: " + user2.getFriends());
    }

    public void deleteFriendById(Long id, Long friendId) {
        User user1 = userStorage.getUserById(id);
        user1.getFriends().remove(friendId);
        log.info("У " + user1 + " теперь в друзьях остались: " + user1.getFriends());
        User user2 =
                userStorage.getUserById(friendId);
        user2.getFriends().remove(id);
        log.info("У " + user2 + " теперь в друзьях остались: " + user2.getFriends());
    }

    public Set<User> getListFriends(Long id) {
        User user = userStorage.getUserById(id);
        return user.getFriends()
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }


    public Set<User> getListFriendsSharedWithAnotherUser(Long id, Long otherId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(otherId);
        final Set<Long> friends = user1.getFriends();
        final Set<Long> otherFriends = user2.getFriends();

        return (Set<User>) friends.stream()
                .filter(otherFriends::contains)
                .map(userId -> userStorage.getUserById(userId))
                .collect(Collectors.toList());
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> listUsers() {
        return userStorage.listUsers();
    }
}
