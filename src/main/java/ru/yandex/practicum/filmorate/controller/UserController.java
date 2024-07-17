package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.debug("updateUser");
        log.debug("--> Step 1. Получено RequestBody: {}", updatedUser);
        int receivedId = updatedUser.getId();
        User oldUser = usersStorage.get(receivedId);
        if (oldUser == null) {
            throw new UserNotFoundException("Пользователь с id " + receivedId + " не найден.");
        }
        if (updatedUser.getName() == null || updatedUser.getName().isEmpty()) {
            updatedUser.setName(updatedUser.getLogin());
        }
        usersStorage.put(receivedId, updatedUser);
        log.debug("--> Step 2. Пользователь обновлен.+" +
                "\noldUser: {}+" +
                "\nupdatedUser: {}", oldUser, updatedUser);
        return updatedUser;
    }
}



