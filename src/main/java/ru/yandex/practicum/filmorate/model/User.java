package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.Marker;

import java.time.LocalDate;

@Data
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private int id;

    private String name;

    @NotNull(groups = Marker.OnCreate.class, message = "Email не может быть null.")
    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Некорректный email: Email не соответстует маске.")
    private String email;

    @NotNull(groups = Marker.OnCreate.class, message = "Логин не может быть null.")
    @NotBlank(message = "Некорректный логин: Логин не может быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "Некорректный логин: Логин не может содержать пробелы.")
    private String login;

    @NotNull(groups = Marker.OnCreate.class, message = "Дата рождения не может быть null.")
    @Past(message = "Некорректная дата рождения: Дата рождения не может быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
