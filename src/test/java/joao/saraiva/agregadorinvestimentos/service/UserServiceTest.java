package joao.saraiva.agregadorinvestimentos.service;

import joao.saraiva.agregadorinvestimentos.controller.CreateUserDto;
import joao.saraiva.agregadorinvestimentos.controller.UpdateUserDto;
import joao.saraiva.agregadorinvestimentos.entity.User;
import joao.saraiva.agregadorinvestimentos.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

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
//            "Quando alguém pedir para você guardar um usuário (save), "
//            "me devolve esse user de exemplo que eu criei, e guarda"
//            "numa caixinha transparente (userArgumentCaptor.capture()) "
//            "o que foi que te pediram para guardar!"
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());
            var input = new CreateUserDto("username",
                                            "email@email.com",
                                            "123"
            );


            //Act
            var output = userService.createUser(input);

            //Assert
            assertNotNull(output);
            var userCaptured = userArgumentCaptor.getValue();
            assertEquals(input.username(), userCaptured.getUsername());
            assertEquals(input.email(), userCaptured.getEmail());
            assertEquals(input.password(), userCaptured.getPassword());
        }

        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {
            //Arrange

//            "Quando alguém pedir para você guardar qualquer coisa (any()),
//            ao invés de guardar, você vai quebrar (doThrow(new RuntimeException()))!"
            doThrow(new RuntimeException()).when(userRepository).save(any());
            var input = new CreateUserDto("username",
                    "email@email.com",
                    "123"
            );


            //Act & Assert
            assertThrows(RuntimeException.class, () -> userService.createUser(input));

        }
    }

    @Nested
    class getUserById {

        @Test
        @DisplayName("Should get user by id with success when optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {

            //Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );

            // "Quando alguém pedir para o userRepository encontrar
            // um usuário pelo ID (findById), eu vou devolver ele
            // enroladinho num papel (Optional.of(user)) e vou guardar
            // numa caixinha o ID que foi usado para buscar."
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            //Act
            var output = userService.getUserById(user.getUserId().toString());

            //Assert
            assertTrue(output.isPresent()); // Como a função é Optional, devemos ver se ele retorna um True
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue()); // Verifica se:
            // "O ID que ele usou para buscar é o ID do nosso usuário de exemplo?"

        }

        @Test
        @DisplayName("Should get user by id with success when optional is empty")
        void shouldGetUserByIdWithSuccessWhenOptionalIsEmpty() {

            //Arrange
            var userId = UUID.randomUUID();
            doReturn(Optional.empty())
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            //Act
            var output = userService.getUserById(userId.toString());

            //Assert
            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentCaptor.getValue());

        }



    }

    @Nested
    class listUsers {

        @Test
        @DisplayName("Should return all users with succes")
        void shouldReturnAllUserWithSucces() {
            //Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            var outputList = List.of(user);
            //Quando alguém pedir para o userRepository encontrar
            //todos (findAll()), eu vou devolver essa lista que eu preparei
            doReturn(outputList)
                    .when(userRepository)
                    .findAll();

            //Act
            var output = userService.listUsers();

            //Assert
            assertNotNull(output);
            assertEquals(outputList.size(), output.size());

        }
    }
    
    @Nested
    class deleteById {

        @Test
        @DisplayName("Should delete user with succes when user exist")
        void shouldDeleteUserWithSuccesWhenUserExist() {

            //Arrange
            doReturn(true)
                    .when(userRepository)
                    .existsById(uuidArgumentCaptor.capture());
            doNothing()
                    .when(userRepository)
                    .deleteById(uuidArgumentCaptor.capture());

            var userId = UUID.randomUUID();

            //Act
            userService.deleteById(userId.toString());

            //Assert
            var idList = uuidArgumentCaptor.getAllValues();
            assertEquals(userId, idList.get(0));
            assertEquals(userId, idList.get(1));

            verify(userRepository, times(1)).existsById(idList.get(0));
            verify(userRepository, times(1)).deleteById(idList.get(1));
        }

        @Test
        @DisplayName("Should not delete user when NOT user exist")
        void shouldNotDeleteUserWhenUserNotExist() {

            //Arrange
            doReturn(false)
                    .when(userRepository)
                    .existsById(uuidArgumentCaptor.capture());

            var userId = UUID.randomUUID();

            //Act
            userService.deleteById(userId.toString());

            //Assert
            assertEquals(userId, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1))
                    .existsById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0))
                    .deleteById(any());
        }
    }

    @Nested
    class updateUserById {

        @Test
        @DisplayName("Should update user by id when user exist and username and password is filled")
        void shouldUpdateUserByIdWhenUserExistAndUsernameAndPasswordIsFilled() {

            //Arrange
            var updateUserDto = new UpdateUserDto(
                    "newUsername",
                    "newPassword"
            );
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            //mockar findById
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());
            //mockar save
            doReturn(user)
                    .when(userRepository)
                    .save(userArgumentCaptor.capture());

            //Act
            userService.updateUserById(user.getUserId().toString(), updateUserDto);

            //Assert
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());

            var userCaptured = userArgumentCaptor.getValue();

            assertEquals(updateUserDto.username(), userCaptured.getUsername());
            assertEquals(updateUserDto.password(), userCaptured.getPassword());

            verify(userRepository, times(1))
                    .findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should not update user when user NOT exist")
        void shouldNotUpdateUserWhenUserNotExist() {

            //Arrange
            var updateUserDto = new UpdateUserDto(
                    "newUsername",
                    "newPassword"
            );
            var userId = UUID.randomUUID();
            //mockar findById
            doReturn(Optional.empty())
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());


            //Act
            userService.updateUserById(userId.toString(), updateUserDto);

            //Assert
            assertEquals(userId, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1))
                    .findById(uuidArgumentCaptor.getValue());
            //Como é senário negativo, não deve salvar nada, ou seja, o save não deve ser chamado
            verify(userRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("Should update user by id when user exist and only username is filled")
        void shouldUpdateUserByIdWhenUserExistAndOnlyUsernameIsFilled() {

            //Arrange
            var updateUserDto = new UpdateUserDto(
                    "newUsername",
                    null
            );
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );

            //mockar findById
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());
            //mockar save
            doReturn(user)
                    .when(userRepository)
                    .save(userArgumentCaptor.capture());

            //Act
            userService.updateUserById(user.getUserId().toString(), updateUserDto);

            //Assert
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());

            var userCaptured = userArgumentCaptor.getValue();
            assertEquals(updateUserDto.username(), userCaptured.getUsername());
            assertEquals(user.getPassword(), userCaptured.getPassword());
            verify(userRepository, times(1))
                    .findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).save(user);

        }

        @Test
        @DisplayName("Should update user by id when user exist and only passwor is filled")
        void shouldUpdateUserByIdWhenUserExistAndOnlyPasswordIsFilled() {

            //Arrange
            var updateUserDto = new UpdateUserDto(
                    null,
                    "newPassword"
            );
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );

            //mockar findById
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());
            //mockar save
            doReturn(user)
                    .when(userRepository)
                    .save(userArgumentCaptor.capture());

            //Act
            userService.updateUserById(user.getUserId().toString(), updateUserDto);

            //Assert
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());

            var userCaptured = userArgumentCaptor.getValue();

            assertEquals(user.getUsername(), userCaptured.getUsername());
            assertEquals(updateUserDto.password(), userCaptured.getPassword());

            verify(userRepository, times(1))
                    .findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1))
                    .save(user);

        }
    }
}

