package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
@Component
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sqlQueryInsert = "insert into USERS(USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY)" +
                " values (?, ?, ?, ?)";
        Long id = writingToTableUsers(user, sqlQueryInsert);
        log.info(user + " Пользователь успешно добавлен.");
        return getUserById(id);
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        String sqlQuery = "update USERS set USER_LOGIN = ?, USER_NAME = ?, USER_EMAIL = ?, USER_BIRTHDAY = ?" +
                " where USER_ID = ?";
        writingToTableUsers(user, sqlQuery);
        log.info(user + " Пользователь успешно обновлен");
        return user;
    }

    @Override
    public List<User> listUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(Long id) {
        try {
            String sqlQuery = "select USER_ID, USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY from USERS" +
                    " where USER_ID = ?";
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
    }

    @Override
    public void addFriendById(Long id, Long friendId) {
        String sqlQueryInsert = "insert into FRIENDSHIP(FRIENDSHIP_USER_ID, FRIENDSHIP_FRIEND_ID)" +
                " values (?, ?)";
        User user1 = getUserById(id);
        User user2 = getUserById(friendId);
        if (!checkFriendshipExits(id, friendId)) {
            jdbcTemplate.update(sqlQueryInsert, user1.getId(), user2.getId());
            log.info("Пользователь " + user1 + " дружит с пользователем " + user2);
        }
    }

    @Override
    public List<User> getListFriends(Long id) {
        if (!userExists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        String sqlQuery = "SELECT u.* " +
                "FROM USERS u " +
                "JOIN FRIENDSHIP f ON u.USER_ID = f.FRIENDSHIP_FRIEND_ID " +
                "WHERE f.FRIENDSHIP_USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            return User.builder()
                    .id(rs.getLong("USER_ID"))
                    .name(rs.getString("USER_NAME"))
                    .login(rs.getString("USER_LOGIN"))
                    .email(rs.getString("USER_EMAIL"))
                    .birthday(rs.getDate("USER_BIRTHDAY").toLocalDate())
                    .build();
        }, id);
    }


    private boolean userExists(Long id) {
        String checkQuery = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(checkQuery, new Long[]{id}, Integer.class) > 0;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT DISTINCT u.* " +
                "FROM USERS u " +
                "JOIN FRIENDSHIP f ON u.USER_ID = f.FRIENDSHIP_FRIEND_ID " +
                "JOIN FRIENDSHIP o ON u.USER_ID = o.FRIENDSHIP_FRIEND_ID " +
                "WHERE f.FRIENDSHIP_USER_ID = ? " +
                "AND o.FRIENDSHIP_USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            return User.builder()
                    .id(rs.getLong("USER_ID"))
                    .name(rs.getString("USER_NAME"))
                    .login(rs.getString("USER_LOGIN"))
                    .email(rs.getString("USER_EMAIL"))
                    .birthday(rs.getDate("USER_BIRTHDAY").toLocalDate())
                    .build();
        }, id, otherId);
    }


    @Override
    public void removeFriendById(Long id, Long friendId) {
        if (!userExists(id) || !userExists(friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        final String sqlQuery = "delete from FRIENDSHIP" +
                " where FRIENDSHIP_USER_ID = ? and FRIENDSHIP_FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("USER_EMAIL"))
                .login(resultSet.getString("USER_LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .birthday(resultSet.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
        return user;
    }

    private Long writingToTableUsers(User user, String query) {
        if (user.getId() == null) {
            return writingToTableUsersWithoutId(user, query);
        } else {
            return writingToTableById(user, query);
        }
    }

    private Long writingToTableUsersWithoutId(User user, String query) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getBirthday().toString());
            return ps;
        }, keyHolder);
        return Long.parseLong(keyHolder.getKey().toString());
    }

    private Long writingToTableById(User user, String query) {
        jdbcTemplate.update(query, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(),
                user.getId());
        return Long.parseLong(user.getId().toString());
    }

    private boolean checkFriendshipExits(Long id, Long friendId) {
        String sqlQuery = "SELECT EXISTS(select * from FRIENDSHIP AS tols_user where FRIENDSHIP_USER_ID = ?" +
                " AND EXISTS (SELECT * FROM FRIENDSHIP WHERE FRIENDSHIP_FRIEND_ID= ?))";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id, friendId}, Boolean.class);
        if (exists) {
            return exists;
        }
        return exists;
    }

}
