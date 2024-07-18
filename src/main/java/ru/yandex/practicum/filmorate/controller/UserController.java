package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> usersStorage =  new HashMap<>();

    private int getNextId() {
        int currMaxId = usersStorage.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currMaxId;
    }

    @GetMapping
    public Collection<User> getUsersList() {
        log.debug("getAllUsers. Возвращаем список всех пользователей.");
        return usersStorage.values();
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public User createUser(@Valid @RequestBody User user) {
        log.debug("createUser");
        log.debug("--> Step 1. Получено RequestBody: {}", user);
        int newId = getNextId();
        user.setId(newId);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        int oldUsersStorageSize = usersStorage.size();
        usersStorage.put(newId, user);
        log.debug("--> Step 2. Новый пользователь сохранен. " +
                "Размер usersStorage увеличился на [{}].", usersStorage.size() - oldUsersStorageSize);
        return user;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public User updateUser(@Valid @RequestBody User updUser) {
        log.debug("updateUser");
        log.debug("--> Step 1. Получено RequestBody: {}", updUser);
        int receivedId = updUser.getId();
        User oldUser = usersStorage.get(receivedId);
        if (oldUser == null) {
            throw new NotFoundException("Запрос на обновление пользователя. Получен id: +" + receivedId +
                    ". Пользователь с данным id не найден.");
        }
        if (updUser.getName() == null || updUser.getName().isBlank()) {
            updUser.setName(updUser.getLogin());
        }
        usersStorage.put(receivedId, updUser);
        log.debug("--> Step 2. Пользователь обновлен.+" +
                "\noldUser: {}+" +
                "\nupdatedUser: {}", oldUser, updUser);
        return updUser;
    }

}



