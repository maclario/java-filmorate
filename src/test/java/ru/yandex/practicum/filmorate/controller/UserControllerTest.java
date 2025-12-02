package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    UserController userController;
    UserStorage userStorage;
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
        userStorage = new InMemoryUserStorage();
        userController = new UserController(new UserService(userStorage));
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
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

    @Test
    public void addFriendsTest() throws Exception {
        String user2Name = "us2" + validName;
        String user2Login = "us2" + validLogin;
        String user2Email = "us2" + validEmail;
        LocalDate user2Birthday = LocalDate.of(1980, 10, 10);

        User user2 = new User();
        user2.setName(user2Name);
        user2.setLogin(user2Login);
        user2.setEmail(user2Email);
        user2.setBirthday(user2Birthday);

        String responseAfterCreateUser = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String responseAfterCreateUser2 = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        int createdUserId = mapper.readValue(responseAfterCreateUser, User.class).getId();
        int createdUser2Id = mapper.readValue(responseAfterCreateUser2, User.class).getId();
        String urlForAddFriend = "/users/" + createdUserId + "/friends/" + createdUser2Id;

        mockMvc.perform(MockMvcRequestBuilders.put(urlForAddFriend))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void removeFriendTest() throws Exception {
        String user2Name = "us2" + validName;
        String user2Login = "us2" + validLogin;
        String user2Email = "us2" + validEmail;
        LocalDate user2Birthday = LocalDate.of(1980, 10, 10);

        String user3Name = "us3" + validName;
        String user3Login = "us3" + validLogin;
        String user3Email = "us3" + validEmail;
        LocalDate user3Birthday = LocalDate.of(1981, 11, 11);

        User user2 = new User();
        user2.setName(user2Name);
        user2.setLogin(user2Login);
        user2.setEmail(user2Email);
        user2.setBirthday(user2Birthday);

        User user3 = new User();
        user3.setName(user3Name);
        user3.setLogin(user3Login);
        user3.setEmail(user3Email);
        user3.setBirthday(user3Birthday);

        String responseAfterCreateUser = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String responseAfterCreateUser2 = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String responseAfterCreateUser3 = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user3)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        int createdUserId = mapper.readValue(responseAfterCreateUser, User.class).getId();
        int createdUser2Id = mapper.readValue(responseAfterCreateUser2, User.class).getId();
        int createdUser3Id = mapper.readValue(responseAfterCreateUser3, User.class).getId();
        String urlForAddFriend = "/users/" + createdUserId + "/friends/" + createdUser2Id;
        String urlForAddFriend2 = "/users/" + createdUserId + "/friends/" + createdUser3Id;

        mockMvc.perform(MockMvcRequestBuilders.put(urlForAddFriend))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(urlForAddFriend2))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String response = mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate + "/" + createdUserId + "/friends"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(createdUser2Id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(createdUser3Id))
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    public void getFriendsTest() throws Exception {
        String user2Name = "us2" + validName;
        String user2Login = "us2" + validLogin;
        String user2Email = "us2" + validEmail;
        LocalDate user2Birthday = LocalDate.of(1980, 10, 10);

        User user2 = new User();
        user2.setName(user2Name);
        user2.setLogin(user2Login);
        user2.setEmail(user2Email);
        user2.setBirthday(user2Birthday);

        String responseAfterCreateUser = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String responseAfterCreateUser2 = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        int createdUserId = mapper.readValue(responseAfterCreateUser, User.class).getId();
        int createdUser2Id = mapper.readValue(responseAfterCreateUser2, User.class).getId();
        String urlForAddFriend = "/users/" + createdUserId + "/friends/" + createdUser2Id;

        mockMvc.perform(MockMvcRequestBuilders.put(urlForAddFriend));
        mockMvc.perform(MockMvcRequestBuilders.delete(urlForAddFriend));

        String response = mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate + "/" + createdUserId + "/friends"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(response, "[]");
    }

    @Test
    public void getCommonFriendsTest() throws Exception {
        String user2Name = "us2" + validName;
        String user2Login = "us2" + validLogin;
        String user2Email = "us2" + validEmail;
        LocalDate user2Birthday = LocalDate.of(1980, 10, 10);

        String user3Name = "us3" + validName;
        String user3Login = "us3" + validLogin;
        String user3Email = "us3" + validEmail;
        LocalDate user3Birthday = LocalDate.of(1981, 11, 11);

        User user2 = new User();
        user2.setName(user2Name);
        user2.setLogin(user2Login);
        user2.setEmail(user2Email);
        user2.setBirthday(user2Birthday);

        User user3 = new User();
        user3.setName(user3Name);
        user3.setLogin(user3Login);
        user3.setEmail(user3Email);
        user3.setBirthday(user3Birthday);

        String responseAfterCreateUser = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String responseAfterCreateUser2 = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        String responseAfterCreateUser3 = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user3)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        int createdUserId = mapper.readValue(responseAfterCreateUser, User.class).getId();
        int createdUser2Id = mapper.readValue(responseAfterCreateUser2, User.class).getId();
        int createdUser3Id = mapper.readValue(responseAfterCreateUser3, User.class).getId();
        String urlForAddFriend = "/users/" + createdUserId + "/friends/" + createdUser2Id;
        String urlForAddFriend2 = "/users/" + createdUser3Id + "/friends/" + createdUser2Id;
        String urlForGetCommonFriends = "/users/" + createdUserId + "/friends/common/" + createdUser3Id;

        mockMvc.perform(MockMvcRequestBuilders.put(urlForAddFriend));
        mockMvc.perform(MockMvcRequestBuilders.put(urlForAddFriend2));

        mockMvc.perform(MockMvcRequestBuilders.get(urlForGetCommonFriends))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(createdUser2Id));
    }

}
