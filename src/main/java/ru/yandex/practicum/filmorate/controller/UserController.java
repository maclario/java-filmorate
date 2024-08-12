package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsersList() {
        log.info("Get запрос: Получение списка всех пользователей");
        log.info("Вызван: userService.getAllUsers()");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Post запрос: Сохранение пользователя. Получено в запросе: {}", user);
        log.info("Вызван: userService.createUser(User user)");
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updUser) {
        log.info("Put запрос: Обновление пользователя. Получено в запросе: {}", updUser);
        log.info("Вызван: userService.updateUser(User updUser)");
        return ResponseEntity.ok(userService.updateUser(updUser));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Put запрос: Добавление в список друзей. Получено в запросе: id = {}, friendId = {}", id, friendId);
        log.info("Вызван: userService.addFriend(int id, int friendId)");
        userService.addFriend(id, friendId);
        return ResponseEntity.ok("Friend added");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Delete запрос: Удаление из списка друзей. Получено в запросе: id = {}, friendId = {}", id, friendId);
        log.info("Вызван: userService.removeFriend(int id, int friendId)");
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok("Friend removed");
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable int id) {
        log.info("Get запрос: Получение списка друзей пользователя. Получено в запросе: id = {}", id);
        log.info("Вызван: userService.getFriends(int id)");
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Get запрос: Получение списка общих друзей. Получено в запросе: id = {}, friendId = {}", id, otherId);
        log.info("Вызван: userService.getCommonFriends(int id, int otherId)");
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }

}



