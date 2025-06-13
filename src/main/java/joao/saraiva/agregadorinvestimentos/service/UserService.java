package joao.saraiva.agregadorinvestimentos.service;

import joao.saraiva.agregadorinvestimentos.controller.CreateUserDto;
import joao.saraiva.agregadorinvestimentos.controller.UpdateUserDto;
import joao.saraiva.agregadorinvestimentos.entity.User;
import joao.saraiva.agregadorinvestimentos.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Ainda precisa para o retorno

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID createUser(CreateUserDto createUserDto){

        // DTO -> ENTITY
        // Crie a entidade sem definir o userId.
        // O Hibernate, devido ao @GeneratedValue, fará isso.
        var entity = new User(
                createUserDto.username(),
                createUserDto.email(),
                createUserDto.password());

        // Para criptografar a senha, adicione um PasswordEncoder aqui, ex:
        // entity.setPassword(passwordEncoder.encode(createUserDto.password()));

        var userSaved = userRepository.save(entity); // Hibernate agora entenderá que é um novo registro.

        return userSaved.getUserId();
    }

    public Optional<User> getUserById(String userId) {

        return userRepository.findById(UUID.fromString(userId));
    }

    public List<User> listUsers(){
        return userRepository.findAll();
    }

    public void updateUserById(String userId,
                               UpdateUserDto updateUserDto) {

        var id = UUID.fromString(userId);
        var userEntity = userRepository.findById(id);

        if (userEntity.isPresent()) {
            var user = userEntity.get();

            if (updateUserDto.username() != null) {
                user.setUsername(updateUserDto.username());
            }
            if (updateUserDto.password() != null) {
                user.setPassword(updateUserDto.password());
            }

            userRepository.save(user);
        }
    }

    public void deleteById(String userId) {

        var id = UUID.fromString(userId);

        var userExist = userRepository.existsById(id);

        if (userExist) {
            userRepository.deleteById(id);
        }

    }


}