package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;

    public void addLikeFilm(Long id, Long userId) {
        userDao.getUserById(userId);
        Film film = filmDao.getFilmById(id);
        filmDao.addLikeFilmToUser(id, userId);
        film.addLike(userId);
        log.info("Пользователь по id: " + userId + " поставил Like фильму " + film);
    }

    public void deleteLikeFilm(Long id, Long userId) {
        userDao.getUserById(userId);
        Film film = filmDao.getFilmById(id);
        film.getLikes().remove(userId);
        filmDao.deleteLikeFilmToUser(id, userId);
        log.info("Пользователь по id: " + userId + " удалил Like фильму " + film);
    }


    public List<Film> getPopularFilms(Integer count) {
        return filmDao.getPopularFilms(count);
    }

    public Film getFilmById(Long id) {
        return filmDao.getFilmById(id);
    }

    public Film addFilm(Film film) {
        validationFilm(film);
        return filmDao.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validationFilm(film);
        return filmDao.updateFilm(film);
    }

    public List<Film> listFilms() {
        return filmDao.listFilms();
    }

    private Film validationFilm(Film film) {
        if (film.getDescription().length() > 200) {
            log.info("Ошибка! Описание фильма больше 200 символов!");
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка! Дата релиза — не может быть раньше 28.12.1895 г.!");
            throw new ValidationException("Дата релиза — не может быть раньше 28.12.1895 г.");
        }
        if (film.getDuration() <= 0) {
            log.info("Ошибка! Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getGenres() == null || film.getLikes() == null) {
            return Film.builder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .mpa(film.getMpa())
                    .genres(new ArrayList<>())
                    .build();
        } else {
            return film;
        }
    }

}
