package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавляем фильм " + film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info(film + " Фильм успешно добавлен!");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновляем фильм " + film);
        if (!films.containsKey(film.getId())) {
            log.info("Ошибка! Такой фильм не найден...!");
            throw new NotFoundException("Ошибка! Такой фильм не найден...");
        }
        films.put(film.getId(), film);
        log.info(film + " Фильм успешно обновлен");
        return film;
    }

    @GetMapping
    public List<Film> listFilms() {
        log.info("Получаем список фильмов, их количество: " + films.size());
        return new ArrayList<>(films.values());
    }
}
