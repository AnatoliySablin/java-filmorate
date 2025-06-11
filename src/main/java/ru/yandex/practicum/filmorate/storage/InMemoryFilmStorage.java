package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;

    public Film addFilm(Film film) {
        log.info("Добавляем фильм {}", film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("{} Фильм успешно добавлен!", film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Обновляем фильм {}", film);
        films.put(film.getId(), film);
        log.info("{} Фильм успешно обновлен", film);
        return film;
    }

    public List<Film> listFilms() {
        log.info("Получаем список фильмов, их количество: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
        return film;
    }
}
