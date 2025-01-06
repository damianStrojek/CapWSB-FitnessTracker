package pl.wsb.fitnesstracker.user.internal;

import pl.wsb.fitnesstracker.IntegrationTest;
import pl.wsb.fitnesstracker.IntegrationTestBase;
import pl.wsb.fitnesstracker.user.api.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class UserApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllUsers_whenGettingAllUsers() throws Exception {

        User user1 = existingUser(generateUser());
        User user2 = existingUser(generateUser());

        List<User> allUserDebug = getAllUsers();

        mockMvc.perform(get("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[10].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[10].lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$[10].birthdate").value(ISO_DATE.format(user1.getBirthdate())))

                .andExpect(jsonPath("$[11].firstName").value(user2.getFirstName()))
                .andExpect(jsonPath("$[11].lastName").value(user2.getLastName()))
                .andExpect(jsonPath("$[11].birthdate").value(ISO_DATE.format(user2.getBirthdate())))

                .andExpect(jsonPath("$[12]").doesNotExist());
    }

    @Test
    void shouldReturnAllSimpleUsers_whenGettingAllUsers() throws Exception {
        User user1 = existingUser(generateUser());
        User user2 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users/simple").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[10].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[10].lastName").value(user1.getLastName()))

                .andExpect(jsonPath("$[11].firstName").value(user2.getFirstName()))
                .andExpect(jsonPath("$[11].lastName").value(user2.getLastName()))

                .andExpect(jsonPath("$[12]").doesNotExist());
    }

    @Test
    void shouldReturnDetailsAboutUser_whenGettingUserById() throws Exception {
        User user1 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users/{userId}", user1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$.birthdate").value(ISO_DATE.format(user1.getBirthdate())))
                .andExpect(jsonPath("$.email").value(user1.getEmail()));

    }

    @Test
    void shouldReturnDetailsAboutUser_whenGettingUserByEmail() throws Exception {
        User user1 = existingUser(generateUser());

        mockMvc.perform(get("/v1/users/email").param("email", user1.getEmail()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user1.getId().intValue()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()));
    }

    @Test
    void shouldReturnAllUsersOlderThan_whenGettingAllUsersYoungerThan() throws Exception {
        User user1 = existingUser(generateUserWithDate(LocalDate.of(2050, 8, 11)));
        User user2 = existingUser(generateUserWithDate(LocalDate.of(2040, 8, 11)));

        mockMvc.perform(get("/v1/users/younger/{time}", LocalDate.of(2049, 8, 10)).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$[0].birthdate").value(ISO_DATE.format(user1.getBirthdate())))

                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    void shouldRemoveUserFromRepository_whenDeletingClient() throws Exception {
        User user1 = existingUser(generateUser());
        List<User> allUserDebug = getAllUsers();

        mockMvc.perform(delete("/v1/users/{userId}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isNoContent());

        List<User> allUser = getAllUsers();
        assertThat(allUserDebug.size()).isGreaterThan(allUser.size());
    }

    @Test
    void shouldPersistUser_whenCreatingUser() throws Exception {

        String USER_NAME = "Mike";
        String USER_LAST_NAME = "Scott";
        String USER_BIRTHDATE = "1999-09-29";
        String USER_EMAIL = "mike.scott@domain.com";

        String creationRequest = """
                                               
                {
                "firstName": "%s",
                "lastName": "%s",
                "birthdate": "%s",
                "email": "%s"
                }
                """.formatted(
                USER_NAME,
                USER_LAST_NAME,
                USER_BIRTHDATE,
                USER_EMAIL);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isCreated());

        List<User> allUsers = getAllUsers();
        User user = allUsers.get(allUsers.size()-1);

        assertThat(user.getFirstName()).isEqualTo(USER_NAME);
        assertThat(user.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.parse(USER_BIRTHDATE));
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);

    }

    @Test
    void  User_whenUpdatingUser() throws Exception {
        User user1 = existingUser(generateUser());

        String USER_NAME = "Mike";
        String USER_LAST_NAME = "Scott";
        String USER_BIRTHDATE = "1999-09-29";
        String USER_EMAIL = "mike.scott@domain.com";

        String updateRequest = """
                                              
                {
                "firstName": "%s",
                "lastName": "%s",
                "birthdate": "%s",
                "email": "%s"
                }
                """.formatted(
                USER_NAME,
                USER_LAST_NAME,
                USER_BIRTHDATE,
                USER_EMAIL);

        mockMvc.perform(put("/v1/users/{userId}", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest));

        List<User> allUsers = getAllUsers();
        User user = allUsers.get(10);

        assertThat(user.getFirstName()).isEqualTo(USER_NAME);
        assertThat(user.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.parse(USER_BIRTHDATE));
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);
    }

    public static User generateUser() {
        return new User(randomUUID().toString(), randomUUID().toString(), LocalDate.now(), randomUUID().toString());
    }

    private static User generateUserWithDate(LocalDate date) {
        return new User(randomUUID().toString(), randomUUID().toString(), date, randomUUID().toString());
    }


}
