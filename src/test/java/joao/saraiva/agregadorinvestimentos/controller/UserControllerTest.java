package joao.saraiva.agregadorinvestimentos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.saraiva.agregadorinvestimentos.entity.User;
import joao.saraiva.agregadorinvestimentos.service.UserService;
import org.apache.coyote.http11.upgrade.UpgradeServletOutputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    }

    @Test
    void getUserById() {
    }

    @Test
    void listUsers() {
    }

    @Test
    void updateUserById() {
    }

    @Test
    void deleteById() {
    }
}