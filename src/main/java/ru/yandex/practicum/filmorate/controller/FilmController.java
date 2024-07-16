package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> filmsStorage = new HashMap<>();

    private int getNextId() {
        int currMaxId = filmsStorage.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currMaxId;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("getAllFilms method. Возвращаем список всех фильмов.");
        return filmsStorage.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("createFilm method.");
        log.debug("--> Step 1. Получено RequestBody: {}", film);
        int newId = getNextId();
        film.setId(newId);
        int oldFilmsStorageSize = filmsStorage.size();
        filmsStorage.put(newId, film);
        log.debug("--> Step 2. Новый пользователь сохранен. +" +
                "Размер filmsStorage увеличился на [{}].", filmsStorage.size() - oldFilmsStorageSize);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.debug("updateFilm method.");
        log.debug("--> Step 1. Получено RequestBody: {}", updatedFilm);
        int receivedId = updatedFilm.getId();
        Film oldFilm = filmsStorage.get(receivedId);
        if (oldFilm == null) {
            throw new FilmNotFoundException("Фильм с id " + receivedId + " не найден.");
        }
        filmsStorage.put(receivedId, updatedFilm);
        log.debug("--> Step 2. Фильм обновлен.+" +
                "\noldFilm: {}" +
                "\nupdatedFilm: {}", oldFilm, updatedFilm);
        return updatedFilm;
    }
}

