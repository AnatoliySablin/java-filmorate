package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    void start() {
        film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2018, 12, 17))
                .duration(60).build();
        filmController = new FilmController();
    }

    @Test
    void getlistFilms() {
        filmController.addFilm(film);

        assertEquals(1, filmController.listFilms().size(), "Количество фильмов не соответствует!");
        assertNotNull(filmController.listFilms(), "Список фильмов пустой!");
    }
}
