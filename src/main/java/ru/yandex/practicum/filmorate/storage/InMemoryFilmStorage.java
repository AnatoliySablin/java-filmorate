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
        try {
            if (film == null) {
                throw new IllegalArgumentException("Фильм не может быть пустым");
            }
            if (!films.containsKey(film.getId())) {
                throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
            }
            log.info("Обновляем фильм {}", film);
            films.put(film.getId(), film);
            log.info("{} Фильм успешно обновлен", film);
            return film;
        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма", e);
            throw e;
        }
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
