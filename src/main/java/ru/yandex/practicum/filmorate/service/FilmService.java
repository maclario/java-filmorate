package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
Fil
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film updFilm) {
        return filmStorage.updateFilm(updFilm);
    }

    public void addLike(Integer filmId, Integer userId) {
        log.info("Получение пользователя с id = {}", userId);
        userStorage.getUserById(userId);
        log.info("Получение фильма с id = {}", filmId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Добавление id пользователя в список лайков");
        film.getUserLikeIdentifiers().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        log.info("Получение пользователя с id = {}", userId);
        userStorage.getUserById(userId);
        log.info("Получение фильма с id = {}", filmId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Удаление id пользователя из списка лайков");
        film.getUserLikeIdentifiers().remove(userId);
    }

    public List<Film> getMostRatedFilms(Integer count) {
        log.info("Вызов stream, возврат результата с помощью toList");
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUserLikeIdentifiers().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

}
