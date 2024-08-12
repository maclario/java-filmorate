package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Get запрос: Получение всех фильмов");
        log.info("Вызван: filmService.getAllFilms()");
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("Post запрос: Сохранение фильма. Получено в запросе: {}", film);
        log.info("Вызван: filmService.createFilm(Film film)");
        return ResponseEntity.ok(filmService.createFilm(film));
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updFilm) {
        log.info("Put запрос: Обновление фильма. Получено в запросе: {}", updFilm);
        log.info("Вызван: filmService.updateFilm(Film updFilm)");
        return ResponseEntity.ok(filmService.updateFilm(updFilm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        log.info("Get запрос: Получение фильма по id. Получено в запросе: id = {}", id);
        log.info("Вызван: filmService.getFilmById(int id)");
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Put запрос: Добавление лайка фильму. Получено в запросе: id = {}, userId = {}", id, userId);
        log.info("Вызван: filmService.addLike(int id, int userId)");
        filmService.addLike(id, userId);
        return ResponseEntity.ok("Film (id: " + id + "), Like by user (id: " + userId + ") added");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<String> removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Delete запрос: Удаление лайка фильму. Получено в запросе: id = {}, userId = {}", id, userId);
        log.info("Вызван: filmService.removeLike(int id, int userId)");
        filmService.removeLike(id, userId);
        return ResponseEntity.ok("Film (id: " + id + "), Like by user (id: " + userId + ") removed");
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getMostRatedFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Get запрос: Получение популярных фильмов. Получено в запросе: count = {}", count);
        log.info("Вызван: filmService.getMostRatedFilms(int count)");
        return ResponseEntity.ok(filmService.getMostRatedFilms(count));
    }

}

