package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.ReleaseDateValidator;

import java.time.LocalDate;

@SpringBootTest
public class FilmControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private Film film;
    private final String urlTemplate = "/films";
    private final String validName = "Властелин колец: Братство кольца";
    private final String validDescription = "Первая часть культовой фэнтази-трилогии Питера Джексона";
    private final LocalDate validReleaseDate = LocalDate.of(2002, 2, 7);
    private final String validReleaseDateString = "2002-02-07";
    private final int validDuration = 178;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new FilmController())
                .setControllerAdvice(new ControllerAdvice())
                .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        film = new Film();
        film.setName(validName);
        film.setDescription(validDescription);
        film.setReleaseDate(validReleaseDate);
        film.setDuration(validDuration);
    }

    @Test
    public void successfulCreateFilmTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(validName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(validDescription))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value(validReleaseDateString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(validDuration));
    }

    @Test
    public void createFilmThatHasBlankNameReturn4xxErrorTest() throws Exception {
        film.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createFilmThatHasReleaseDateEarlierMinDateReturn4xxErrorTest() throws Exception {
        LocalDate minDate = ReleaseDateValidator.getMinReleaseDate();
        film.setReleaseDate(minDate.minusDays(1));

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createFilmThatHasDescriptionLenMoreThan200Return4xxErrorTest() throws Exception {
        String invalidDescription = "C".repeat(201);
        film.setDescription(invalidDescription);

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createFilmThatHasNegativeDurationValueReturn4xxErrorTest() throws Exception {
        film.setDuration(-120);

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void successfulUpdateFilmTest() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(film)))
                .andReturn().getResponse().getContentAsString();

        int createdFilmId = mapper.readValue(response, Film.class).getId();
        String newName = "Новое название: Звездные войны, Эпизод 3, Месть Ситхов";
        String newDescription = "Новое описание: Галактику терзает война клонов.";
        LocalDate newReleaseDate = LocalDate.of(2005, 5, 12);
        String newReleaseDateString = "2005-05-12";
        int newDuration = 180;

        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilmId);
        updatedFilm.setName(newName);
        updatedFilm.setDescription(newDescription);
        updatedFilm.setReleaseDate(newReleaseDate);
        updatedFilm.setDuration(newDuration);

        mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updatedFilm)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdFilmId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(newDescription))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value(newReleaseDateString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(newDuration));
    }

    @Test
    public void ShouldReturnNotFoundIfFilmsStorageNotContainsIdFromRequestBodyTest() throws Exception {
        film.setId(1);

        mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test void getAllFilmsTest() throws Exception {
        String validName2 = "Тьма";
        String validDescription2 = "История 4 семей, живущих в маленьком немецком городке";
        LocalDate validReleaseDate2 = LocalDate.of(2017, 9,9);
        String validReleaseDate2String = "2017-09-09";
        int validDuration2 = 1300;

        String validName3 = "Начало";
        String validDescription3 = "Профессиональные воры внедряются в сон наследника корпорации.";
        LocalDate validReleaseDate3 = LocalDate.of(2010, 7,8);
        String validReleaseDate3String = "2010-07-08";
        int validDuration3 = 148;

        Film film2 = new Film();
        film2.setName(validName2);
        film2.setDescription(validDescription2);
        film2.setReleaseDate(validReleaseDate2);
        film2.setDuration(validDuration2);

        Film film3 = new Film();
        film3.setName(validName3);
        film3.setDescription(validDescription3);
        film3.setReleaseDate(validReleaseDate3);
        film3.setDuration(validDuration3);

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film3)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(validName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value(validDescription))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].releaseDate").value(validReleaseDateString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].duration").value(validDuration))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value(validName2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value(validDescription2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].releaseDate").value(validReleaseDate2String))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].duration").value(validDuration2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].name").value(validName3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].description").value(validDescription3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].releaseDate").value(validReleaseDate3String))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].duration").value(validDuration3));
    }

}
