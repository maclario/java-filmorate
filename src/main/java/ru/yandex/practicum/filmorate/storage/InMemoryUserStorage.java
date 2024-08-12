package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersStorage =  new HashMap<>();

    private int getNextId() {
        int currMaxId = usersStorage.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currMaxId;
    }

    @Override
    public User createUser(User user) {
        log.info("createUser");
        log.info("--> Step 1. Получено: {}", user);
        int newId = getNextId();
        user.setId(newId);
        log.info("--> Step 2. Пользователю присвоен id = {}", newId);
        int oldUsersStorageSize = usersStorage.size();
        usersStorage.put(newId, user);
        log.info("--> Step 3. Новый пользователь сохранен. " +
                "Размер usersStorage увеличился на {}.", usersStorage.size() - oldUsersStorageSize);
        return user;
    }

    @Override
    public User updateUser(User updUser) {
        log.info("updateUser");
        log.info("--> Step 1. Получено RequestBody: {}", updUser);
        int receivedId = updUser.getId();
        User oldUser = getUserById(receivedId);
        usersStorage.put(receivedId, updUser);
        log.info("--> Step 2. Пользователь обновлен.+" +
                "\noldUser: {}+" +
                "\nupdatedUser: {}", oldUser, updUser);
        return updUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("getAllUsers. Возвращаем список всех пользователей.");
        return usersStorage.values();
    }

    @Override
    public User getUserById(Integer id) {
        User requestedUser = usersStorage.get(id);
        if (requestedUser == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return requestedUser;
    }

}
