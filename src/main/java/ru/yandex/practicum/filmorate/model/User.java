package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private Set<Long> friends;
    @NotBlank
    @Email
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;
    @Size(max = 20, message = "Имя не может быть длиннее 20 символов")
    private String name;
    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<Long>();
        }
        return friends;
    }

    public void addFriend(Long id) {
        if (friends == null) {
            friends = new HashSet<Long>();
            friends.add(id);
        } else {
            friends.add(id);
        }
    }


}
