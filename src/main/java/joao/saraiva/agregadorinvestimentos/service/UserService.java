package joao.saraiva.agregadorinvestimentos.service;

import joao.saraiva.agregadorinvestimentos.controller.CreateUserDto;
import joao.saraiva.agregadorinvestimentos.entity.User;
import joao.saraiva.agregadorinvestimentos.repository.UserRepository;
import org.springframework.stereotype.Service;

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
}