package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("В запросе не задано имя, установлен логин {} в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User updUser) {
        if (updUser.getName() == null || updUser.getName().isBlank()) {
            log.info("В запросе не задано имя, установлен логин {} в качестве имени", updUser.getLogin());
            updUser.setName(updUser.getLogin());
        }
        return userStorage.updateUser(updUser);
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Получение пользователя с id = {}", userId);
        User user1 = userStorage.getUserById(userId);
        log.info("Вызов метода getFriendsIdentifiers()");
        user1.getFriendsIdentifiers().add(friendId);
        log.info("Id = {} добавлен в список идентификаторов друзей пользователя id = {}", friendId, userId);

        log.info("Получение пользователя с id = {}", friendId);
        User user2 = userStorage.getUserById(friendId);
        log.info("Вызов метода getFriendsIdentifiers()");
        user2.getFriendsIdentifiers().add(userId);
        log.info("Id = {} добавлен в список идентификаторов друзей пользователя id = {}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        log.info("Получение пользователя с id = {}, удаление из списка друзей id = {}", userId, friendId);
        userStorage.getUserById(userId)
                .getFriendsIdentifiers()
                .remove(friendId);

        log.info("Получение пользователя с id = {}, удаление из списка друзей id = {}", friendId, userId);
        userStorage.getUserById(friendId)
                .getFriendsIdentifiers()
                .remove(userId);
    }

    public Collection<User> getFriends(Integer userId) {
        log.info("Вызов stream, возврат результата с помощью toList");
        return userStorage.getUserById(userId)
                .getFriendsIdentifiers().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherUserId) {
        log.info("Получение списка друзей пользователя с id = {}", userId);
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriendsIdentifiers();

        log.info("Получение списка друзей пользователя с id = {}", otherUserId);
        Set<Integer> otherUserFriends = userStorage.getUserById(otherUserId).getFriendsIdentifiers();

        log.info("Вызов stream, возврат результата с помощью toList");
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

}
