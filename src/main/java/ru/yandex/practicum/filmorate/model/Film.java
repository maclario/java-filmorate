package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.Marker;
import ru.yandex.practicum.filmorate.util.ReleaseDate;

import java.time.LocalDate;

@Data
public class Film {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private int id;

    @NotNull(groups = Marker.OnCreate.class, message = "Название фильма не может быть null.")
    @NotBlank(message = "Название не может быть пустым.")
    private String name;

    @NotNull(groups = Marker.OnCreate.class, message = "Описание фильма не может быть null.")
    @NotBlank(message = "Описание не может быть пустым.")
    @Size(max = 200, message = "Максмальная длина описания 200 символов.")
    private String description;

    @NotNull(groups = Marker.OnCreate.class, message = "Дата релиза не может быть null.")
    @ReleaseDate(message = "Релиз фильма должен быть позже, чем 28.12.1985")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull(groups = Marker.OnCreate.class, message = "Продолжительность фильма не может быть null.")
    @Positive(message = "Продолжительность фильма может быть только положительным значением.")
    private int duration;
}
