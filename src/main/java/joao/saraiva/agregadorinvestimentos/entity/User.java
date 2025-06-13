package joao.saraiva.agregadorinvestimentos.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate vai gerar o UUID
    private UUID userId;

    @Column(name = "username", unique = true) // Geralmente username e email são únicos
    private String username;

    @Column(name = "email", unique = true) // Geralmente username e email são únicos
    private String email;

    @Column(name = "password")
    private String password;

    @CreationTimestamp // Hibernate preenche automaticamente na criação
    private Instant creationTimestamp;

    @UpdateTimestamp // Hibernate preenche automaticamente na atualização
    private Instant updateTimestamp;

    public User() {
        // Construtor padrão necessário para JPA
    }

    public User(UUID userId, String username, String email, String password, Instant creationTimestamp, Instant updateTimestamp) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
    }

    // Você pode ter um construtor para facilitar a criação, mas SEM o userId
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Remova o construtor que aceita UUID userId se você não o usa para carregar
    // Se o tiver, ele não deve ser usado para criar novos usuários, apenas para carregar existentes.

    // ... (Getters e Setters como antes)
    // Seus getters e setters estão corretos
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Instant updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}