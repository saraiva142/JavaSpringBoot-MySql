package joao.saraiva.agregadorinvestimentos.service;

import joao.saraiva.agregadorinvestimentos.controller.CreateUserDto;
import joao.saraiva.agregadorinvestimentos.entity.User;
import joao.saraiva.agregadorinvestimentos.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    //Trple A:
    //Arrange -> Arrumar e organizar tudo que precisa para o teste
    //Act -> Chamar o que queremos testar no teste
    //Assert -> Verificações se fez tudo o que tinha que fazer

    //Uma das dependências da nossa Service é o userRepository, então
    //temos que mockar ela e depois fazer a injeção

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    //Criar uma subclasse com Nested para ficar mais organizado

    @Nested
    class createUser {

        @Test
        @DisplayName("should create a user with success")
        void shouldCreateAUserWithSuccess() {

            //Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            doReturn(user).when(userRepository).save(any());
            var input = new CreateUserDto("username",
                                            "email@email.com",
                                            "123"
            );


            //Act
            var output = userService.createUser(input);

            //Assert
            assertNotNull(output);
        }
    }


}