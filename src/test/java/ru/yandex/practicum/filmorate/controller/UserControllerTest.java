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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private User user;
    private final String urlTemplate = "/users";
    private final String validName = "Valid Name";
    private final String validEmail = "valid@email.com";
    private final String validLogin = "ValidLogin";
    private final LocalDate validBirthday = LocalDate.of(1990, 6, 10);
    private final String validBirthdayString = "1990-06-10";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController())
                .setControllerAdvice(new ControllerAdvice())
                .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        user = new User();
        user.setName(validName);
        user.setEmail(validEmail);
        user.setLogin(validLogin);
        user.setBirthday(validBirthday);
    }

    @Test
    public void successfulCreateUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(validName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(validEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(validLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(validBirthdayString));
    }

    @Test
    public void createUserThatHasBlankNameTest() throws Exception {
        user.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(validLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(validEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(validLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(validBirthdayString));
    }

    @Test
    public void createUserThatHasEmptyEmailReturn4xxErrorTest() throws Exception {
        user.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserThatHasInvalidEmailReturn4xxErrorCase1Test() throws Exception {
        user.setEmail("invalidmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserThatHasInvalidEmailReturn4xxErrorCase2Test() throws Exception {
        user.setEmail("@invalidmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserThatHasInvalidLoginReturn4xxErrorTest() throws Exception {
        user.setLogin("invalid loginWithWhitespace");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserThatHasEmptyLoginReturn4xxErrorTest() throws Exception {
        user.setLogin(" ");

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserThatHasFutureBirthdayReturn4xxErrorTest() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void successfulUpdateUserTest() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();


        int createdUserId = mapper.readValue(response, User.class).getId();
        String updName = "updated" + validName;
        String updLogin = "updated" + validLogin;
        String updEmail = "updated" + validEmail;
        LocalDate updBirthday = LocalDate.of(1980, 10, 10);
        String updBirthdayString = "1980-10-10";


        User updUser = new User();
        updUser.setId(createdUserId);
        updUser.setName(updName);
        updUser.setLogin(updLogin);
        updUser.setEmail(updEmail);
        updUser.setBirthday(updBirthday);

        mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdUserId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(updLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value(updBirthdayString));
    }

    @Test
    public void shouldReturnNotFoundIfUsersStorageNotContainsIdFromRequestBodyTest() throws Exception {
        user.setId(3);

        mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        User user2 = new User();
        LocalDate validBirthday2 = LocalDate.of(1980, 10, 10);
        String validBirthday2String = "1980-10-10";
        user2.setName("second" + validName);
        user2.setEmail("second" + validEmail);
        user2.setLogin("second" + validLogin);
        user2.setBirthday(validBirthday2);

        User user3 = new User();
        LocalDate validBirthday3 = LocalDate.of(1970, 5, 12);
        String validBirthday3String = "1970-05-12";
        user3.setName("third" + validName);
        user3.setEmail("third" + validEmail);
        user3.setLogin("third" + validLogin);
        user3.setBirthday(validBirthday3);

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user3)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(validName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email").value(validEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].login").value(validLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].birthday").value(validBirthdayString))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("second" + validName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].email").value("second" + validEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].login").value("second" + validLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].birthday").value(validBirthday2String))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].name").value("third" + validName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].email").value("third" + validEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].login").value("third" + validLogin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].birthday").value(validBirthday3String));
    }

}
