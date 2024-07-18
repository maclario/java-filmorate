package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
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
        log.debug("getAllFilms. Возвращаем список всех фильмов.");
        return filmsStorage.values();
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("createFilm");
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
    @Validated(Marker.OnUpdate.class)
    public Film updateFilm(@Valid @RequestBody Film updFilm) {
        log.debug("updateFilm");
        log.debug("--> Step 1. Получено RequestBody: {}", updFilm);
        int receivedId = updFilm.getId();
        Film oldFilm = filmsStorage.get(receivedId);
        if (oldFilm == null) {
            throw new NotFoundException("Запрос на обновление фильма. Получен id: +" + receivedId +
                    ". Фильм с данным id не найден.");
        }
        filmsStorage.put(receivedId, updFilm);
        log.debug("--> Step 2. Фильм обновлен.+" +
                "\noldFilm: {}" +
                "\nupdatedFilm: {}", oldFilm, updFilm);
        return updFilm;
    }

}

