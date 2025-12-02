package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmsStorage = new HashMap<>();

    private int getNextId() {
        int currMaxId = filmsStorage.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currMaxId;
    }

    @Override
    public Film createFilm(Film film) {
        log.info("createFilm");
        log.info("--> Step 1. Получено: {}", film);
        int newId = getNextId();
        film.setId(newId);
        log.info("--> Step 2. Фильму присвоен id = {}", newId);
        int oldFilmsStorageSize = filmsStorage.size();
        filmsStorage.put(newId, film);
        log.info("--> Step 3. Новый пользователь сохранен. +" +
                "Размер filmsStorage увеличился на {}.", filmsStorage.size() - oldFilmsStorageSize);
        return film;
    }

    @Override
    public Film updateFilm(Film updFilm) {
        log.info("updateFilm");
        log.info("--> Step 1. Получено: {}", updFilm);
        int receivedId = updFilm.getId();
        Film oldFilm = getFilmById(receivedId);
        filmsStorage.put(receivedId, updFilm);
        log.info("--> Step 2. Фильм обновлен.+" +
                "\noldFilm: {}" +
                "\nupdatedFilm: {}", oldFilm, updFilm);
        return updFilm;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmsStorage.values();
    }

    @Override
    public Film getFilmById(Integer id) {
        Film requestedFilm = filmsStorage.get(id);
        if (requestedFilm == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        return requestedFilm;
    }

}
