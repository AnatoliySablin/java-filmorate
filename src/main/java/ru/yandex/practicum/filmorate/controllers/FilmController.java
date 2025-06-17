package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавляем фильм " + film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновляем фильм " + film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получаем фильм по id: " + id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь по id: " + userId + " ставит лайк фильму по id: " + id);
        filmService.addLikeFilm(id, userId);
    }

    @GetMapping
    public List<Film> listFilms() {
        log.info("Получаем список фильмов, их количество: " + filmService.listFilms().size());
        return filmService.listFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Показываем по популярности фильмы");
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь по id: " + userId + " удаляет лайк фильму по id: " + id);
        filmService.deleteLikeFilm(id, userId);
    }
}
