package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film createFilm(Film film);
    Film updateFilm(Film updFilm);
    Film getFilmById(Integer filmId);
    Collection<Film> getAllFilms();
}
