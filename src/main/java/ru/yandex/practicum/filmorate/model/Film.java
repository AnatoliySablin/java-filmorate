package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @Pattern(regexp = "^[1-6]$", message = "id MPA должен быть от 1 до 6")
    private Mpa mpa;
    private List<Genre> genres;

    public List<Genre> getGenres() {
        if (genres == null) {
            genres = new ArrayList<>();
        }
        return genres;
    }

    public void setMpa(Mpa mpa) {
        this.mpa = mpa;
    }

    public Set<Long> getLikes() {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes;
    }

    public void addLike(Long id) {
        if (likes == null) {
            likes = new HashSet<>();
            likes.add(id);
        } else {
            likes.add(id);
        }
    }
}
