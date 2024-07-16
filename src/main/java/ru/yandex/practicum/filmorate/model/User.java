package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    private int id;
    private String name;

    @NotBlank
    @Email(message = "Некорректный email: Email не соответстует маске.")
    private String email;

    @NotBlank(message = "Некорректный логин: Логин не может быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "Некорректный логин: Логин не может содержать пробелы.")
    private String login;

    @Past(message = "Некорректная дата рождения: Дата рождения не может быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
