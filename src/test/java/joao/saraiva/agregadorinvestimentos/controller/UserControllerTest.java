package joao.saraiva.agregadorinvestimentos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.saraiva.agregadorinvestimentos.entity.User;
import joao.saraiva.agregadorinvestimentos.service.UserService;
import org.apache.coyote.http11.upgrade.UpgradeServletOutputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class createUser {

        @Test
        @DisplayName("Should create User with success and return 200")
        void createUserWithSuccess() throws Exception {
            //Arrange
            User user = new User();
            user.setUserId(UUID.randomUUID());
            user.setUsername("Username");
            user.setPassword("password");
            user.setEmail("first@email.com");
            user.setCreationTimestamp(Instant.now());
            user.setUpdateTimestamp(null);

            User expectedUser = new User();
            expectedUser.setUserId(user.getUserId());
            expectedUser.setUsername(user.getUsername());
            expectedUser.setPassword(user.getPassword());
            expectedUser.setEmail(user.getEmail());
            expectedUser.setCreationTimestamp(user.getCreationTimestamp());
            expectedUser.setUpdateTimestamp(user.getUpdateTimestamp());

            when(userService.createUser(any(CreateUserDto.class))).thenReturn(expectedUser.getUserId());

            //Act & Assert
            mockMvc.perform(post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/v1/users" + expectedUser.getUserId().toString()));

            verify(userService, times(1)).createUser(any(CreateUserDto.class));
        }

        @Test
        @DisplayName("Should return empty when no create user")
        void createUserEmpty() throws Exception {
            //Arrange
            User user = new User();
            user.setUserId(UUID.randomUUID());

            when(userService.createUser(any(CreateUserDto.class))).thenReturn(user.getUserId());

            //Act & Assert
            mockMvc.perform(post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/v1/users" + user.getUserId()));

            verify(userService, times(1)).createUser(any(CreateUserDto.class));

        }

//        @Test
//        @DisplayName("Should return error when create user fails")
//        void createUserFail() throws Exception {
//            //Arrange
//            User user = new User();
//
//            when(userService.createUser(any(CreateUserDto.class)))
//                    .thenThrow(new IllegalArgumentException("Invalid user data"));
//
//            //Act & Assert
//            mockMvc.perform(post("/v1/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(user)))
//                    .andExpect(status().isInternalServerError());
//
//            verify(userService, times(1)).createUser(any(CreateUserDto.class));
//        }

    }

    @Nested
    class getUserById {

        @Test
        @DisplayName("Should get a user by id successfully")
        void getUserByIdWithSuccess() throws Exception {
            //Arrange
            UUID id = UUID.randomUUID();
            User user = new User();
            user.setUserId(id);
            user.setUsername("Username");
            user.setPassword("Password");
            user.setEmail("first@email.com");
            user.setCreationTimestamp(Instant.now());
            user.setUpdateTimestamp(null);

            when(userService.getUserById(id.toString())).thenReturn(Optional.of(user));

            //Act & Assert
            mockMvc.perform(get("/v1/users/{userId}", id.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))) //Não precisa mas ok
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(id.toString()))
                    .andExpect(jsonPath("$.username").value(user.getUsername()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()));

            verify(userService, times(1)).getUserById(id.toString());

        }

        @Test
        @DisplayName("Should return 404 not found when user is not found")
        void getUserByIdWhenUserNotFound() throws Exception {
            //Arrange
            UUID id = UUID.randomUUID();

            when(userService.getUserById(id.toString())).thenReturn(Optional.empty());

            //Act && Assert
            mockMvc.perform(get("/v1/users/{userId}", id.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).getUserById(id.toString());

        }
    }

    @Nested
    class listUsers {

        @Test
        @DisplayName("Should return a users list with success")
        void listUsersWithSuccess() throws Exception {
            //Arrange
            UUID id = UUID.randomUUID();

            User user1 = new User();
            user1.setUserId(id);
            user1.setUsername("Username");
            user1.setPassword("pass123");
            user1.setEmail("email@email.com");
            user1.setCreationTimestamp(Instant.now());
            user1.setUpdateTimestamp(null);

            User user2 = new User();
            user2.setUserId(id);
            user2.setUsername("Username 2");
            user2.setPassword("password");
            user2.setEmail("email@email.com");
            user2.setCreationTimestamp(Instant.now());
            user2.setUpdateTimestamp(null);

            List<User> userList = Arrays.asList(user1, user2);

            when(userService.listUsers()).thenReturn(userList);

            //Act && Assert
            mockMvc.perform(get("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].userId").value(user1.getUserId().toString()))
                    .andExpect(jsonPath("$[1].userId").value(user2.getUserId().toString()))
                    .andExpect(jsonPath("$[0].username").value(user1.getUsername()))
                    .andExpect(jsonPath("$[1].username").value(user2.getUsername()));

            assertNotNull(userList);
            assertEquals(2, userList.size());
            verify(userService, times(1)).listUsers();

        }

        @Test
        @DisplayName("Should return empty list when are no have users")
        void listUsersWhenListIsEmpty() throws Exception {
            //Arrange
            List<User> userList = Arrays.asList();

            when(userService.listUsers()).thenReturn(userList);

            //Act && Assert
            mockMvc.perform(get("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            assertTrue(userList.isEmpty());
            assertEquals(0, userList.size());
            verify(userService, times(1)).listUsers();
        }

    }

    @Nested
    class updateUserById {

        @Test
        @DisplayName("Should update user by Id with success")
        void updateUserByIdWithSuccess() throws Exception {
            //Arrange
            UUID id = UUID.randomUUID();

            User userOld = new User();
            userOld.setUserId(id);
            userOld.setUsername("Username Antigo");
            userOld.setEmail("antigo@email.com");
            userOld.setPassword("passOld123");
            userOld.setCreationTimestamp(Instant.now());
            userOld.setUpdateTimestamp(null);

            UpdateUserDto userUpdate = new UpdateUserDto(
                    "Username Novo",
                    "passNew123"
            );
//
//            User userUpdate = new User();
//            userUpdate.setUserId(userOld.getUserId());
//            userUpdate.setUsername("Username Novo");
//            userUpdate.setEmail("novo@email.com");
//            userUpdate.setPassword("passNew123");
//            userUpdate.setCreationTimestamp(Instant.now());
//            userUpdate.setUpdateTimestamp(null);

            User userNew = new User();
            userNew.setUserId(id);
            userNew.setUsername(userUpdate.username());
            userNew.setEmail(userOld.getEmail());
            userNew.setPassword(userUpdate.password());
            userNew.setCreationTimestamp(userOld.getCreationTimestamp());
            userNew.setUpdateTimestamp(Instant.now());

            doNothing().when(userService).updateUserById(eq(id.toString()), any(UpdateUserDto.class));


            //Act & Assert
            mockMvc.perform(put("/v1/users/{id}", id.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userUpdate)))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).updateUserById(eq(id.toString()), any(UpdateUserDto.class));

        }

    }

    @Nested
    class deleteById {

        @Test
        @DisplayName("Should delete a user by id with success")
        void deleteByIdWithSuccess() throws Exception {
            //Arrange
            UUID id = UUID.randomUUID();

            doNothing().when(userService).deleteById(id.toString());

            //Act && Assert
            mockMvc.perform(delete("/v1/users/{id}", id.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
            verify(userService, times(1)).deleteById(eq(id.toString()));
        }

    }
}