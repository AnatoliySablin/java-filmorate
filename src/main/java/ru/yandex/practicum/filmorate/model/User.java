package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
    private String name;
    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<Long>();
        }
        return friends;
    }

    public void setFriends(Long id) {
        if (friends == null) {
            friends = new HashSet<Long>();
            friends.add(id);
        } else {
            friends.add(id);
        }
    }
}
