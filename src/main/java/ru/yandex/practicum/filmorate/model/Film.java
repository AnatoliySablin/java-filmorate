package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    private Set<Long> likes;
    @NotBlank
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    public Set<Long> getLikes() {
        if (likes == null) {
            likes = new HashSet<Long>();
        }
        return likes;
    }

    public void setLikes(Long id) {
        if (likes == null) {
            likes = new HashSet<Long>();
            likes.add(id);
        } else {
            likes.add(id);
        }
    }
}
