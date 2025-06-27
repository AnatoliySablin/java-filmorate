package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Component
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmDao {

    private final MpaDao mpaDao;

    private final GenreDao genreDao;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        final String sqlQueryInsert = "insert into FILMS(NAME, DESCRIPTION, RELEASE_DATE" +
                ", DURATION, FILM_MPA)" +
                " values (?, ?, ?, ?, ?)";
        Long id = writingToTable(film, sqlQueryInsert);
        Film film1 = getFilmById(id);
        log.info(film1 + " Фильм успешно добавлен!");
        return film1;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        final String sqlQuery = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?," +
                " DURATION = ?, FILM_MPA = ? where ID = ?";
        Long id = writingToTable(film, sqlQuery);
        film = getFilmById(id);
        log.info(film + " Фильм успешно обновлен");
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        int limit = (count != null) ? count : 10;

        String sql = "SELECT " +
                "f.*, " +
                "m.MPA_NAME, " +    // Добавляем выборку имени MPA
                "(SELECT COUNT(*) FROM FILM_LIKES fl " +
                "WHERE fl.FILMS_LIKES_ID = f.ID) as LIKE_COUNT " +
                "FROM FILMS f " +
                "INNER JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +  // Добавляем JOIN
                "ORDER BY LIKE_COUNT DESC, f.ID " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = Film.builder()
                    .id(rs.getLong("ID"))
                    .likes(new HashSet<>())
                    .name(rs.getString("NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration((int) rs.getLong("DURATION"))
                    .mpa(new Mpa(
                            rs.getInt("FILM_MPA"),
                            rs.getString("MPA_NAME")))  // Теперь получаем MPA_NAME из JOIN
                    .build();

            return film;
        }, limit);
    }


    @Override
    public List<Film> listFilms() {
        final String sqlQuery = "SELECT \n" +
                "F.ID, \n" +
                "F.NAME, \n" +
                "F.DESCRIPTION, \n" +
                "F.RELEASE_DATE, \n" +
                "F.DURATION, \n" +
                "F.FILM_MPA, \n" +
                "M.MPA_NAME,\n" +
                "(SELECT GROUP_CONCAT(L.FILM_LIKES_USER_ID_WHO_LIKE_FILM) \n" +
                "FROM FILM_LIKES L \n" +
                "WHERE L.FILMS_LIKES_ID = F.ID) AS LIKERS\n" +
                "FROM FILMS F\n" +
                "JOIN MPA M ON M.MPA_ID = F.FILM_MPA;\n";

        List<Film> filmList = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Film film = Film.builder()
                    .id(rs.getLong("ID"))
                    .likes(new HashSet<>())
                    .name(rs.getString("NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration((int) rs.getLong("DURATION"))
                    .mpa(new Mpa(rs.getInt("FILM_MPA"), rs.getString("MPA_NAME")))
                    .build();

            String likersString = rs.getString("LIKERS");
            if (likersString != null && !likersString.isEmpty()) {
                String[] likerIds = likersString.split(",");
                Set<Long> likes = Arrays.stream(likerIds)
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                film.setLikes(likes);
            }

            return film;
        });

        Map<Long, Film> filmMap = new HashMap<>();
        for (Film film : filmList) {
            filmMap.put(film.getId(), film);
        }
        genreDao.setGenresForFilms(filmMap);
        return new ArrayList<>(filmMap.values());
    }


    @Override
    public Film getFilmById(Long id) {
        try {
            final String sqlQuery = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, " +
                    "FILM_MPA, MPA_NAME FROM FILMS INNER JOIN MPA ON FILMS.FILM_MPA = MPA.MPA_ID WHERE ID=?";
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            film.setGenres(genreDao.getListGenresByMovieId(id));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм по id: " + id + " не найден!");
        }
    }

    @Override
    public void addLikeFilmToUser(Long id, Long userId) {
        final String sqlQueryInsertLike = "insert into FILM_LIKES(FILMS_LIKES_ID, FILM_LIKES_USER_ID_WHO_LIKE_FILM)" +
                " values (?, ?)";
        jdbcTemplate.update(sqlQueryInsertLike, id, userId);
    }

    @Override
    public void deleteLikeFilmToUser(Long id, Long userId) {
        final String sqlQuery = "delete from FILM_LIKES" +
                " where FILMS_LIKES_ID = ? and FILM_LIKES_USER_ID_WHO_LIKE_FILM = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    private Long writingToTable(Film film, String query) {
        if (film.getId() == null) {
            return writingToTableWithoutId(film, query);
        } else {
            return writingToTableById(film, query);
        }
    }

    private Long writingToTableWithoutId(Film film, String query) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setString(3, film.getReleaseDate().toString());
                ps.setLong(4, film.getDuration());
                ps.setLong(5, film.getMpa().getId());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
        if (film.getGenres().size() > 0) {
            final String sqlQuery = "INSERT INTO FILM_GENRES(FILM_GENRES_ID, FILM_GENRES_GENRES_ID) VALUES ( ?, " +
                    "? );";
            try {
                jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, Long.parseLong(Objects.requireNonNull(keyHolder.getKey()).toString()));
                        ps.setLong(2, film.getGenres().get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
            } catch (DataAccessException e) {
                throw new NotFoundException(e.getMessage());
            }
        }
        return Long.parseLong(Objects.requireNonNull(keyHolder.getKey()).toString());
    }

    private Long writingToTableById(Film film, String query) {
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));

        final String sqlQueryListGenges = "select FILM_GENRES_GENRES_ID from FILM_GENRES" +
                " where FILM_GENRES_ID = ?";
        List<Long> listIdGenres = jdbcTemplate.queryForList(sqlQueryListGenges,
                new Long[]{Long.parseLong(film.getId().toString())}, Long.class);

        final String sqlQueryGenreDeleteById = "delete from FILM_GENRES where FILM_GENRES_ID = ?" +
                " and FILM_GENRES_GENRES_ID = ?";

        for (Long idGenre : listIdGenres) {
            jdbcTemplate.update(sqlQueryGenreDeleteById, Long.parseLong(film.getId().toString()), idGenre);
        }

        if (!film.getGenres().isEmpty()) {
            Set<Long> myList = new HashSet<Long>();
            for (Genre genreId : film.getGenres()) {
                myList.add((long) genreId.getId());
            }
            final String sqlQueryFilmGenres = "insert into FILM_GENRES(FILM_GENRES_ID, FILM_GENRES_GENRES_ID)" +
                    " values (?, ?)";
            for (Long aLong : myList) {
                jdbcTemplate.update(sqlQueryFilmGenres, film.getId(), aLong);
            }
        }

        return Long.parseLong(film.getId().toString());
    }


    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .mpa(new Mpa(resultSet.getInt("FILM_MPA"), resultSet.getString("MPA_NAME")))
                .build();
    }


}
