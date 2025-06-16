package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public void addLikeFilm(Long id, Long userId) {
        userService.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        film.addLike(userId);
        log.info("Пользователь по id: " + userId + " поставил Like фильму " + film);
    }

    public void deleteLikeFilm(Long id, Long userId) {
        userService.getUserById(userId);
        Film film =
                filmStorage.getFilmById(id);
        film.getLikes().remove(userId);
        log.info("Пользователь по id: " + id + " удалил Like фильму " + film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.listFilms()
                .stream()
                .sorted(Comparator.comparingLong(o -> o.getLikes().size()))
                .limit(count != null ? count : 10)
                .collect(Collectors.toList());
    }


    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.listFilms().contains(film)) {
            throw new NotFoundException("Ошибка! Такой фильм не найден...");
        }
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> listFilms() {
        return filmStorage.listFilms();
    }
}
