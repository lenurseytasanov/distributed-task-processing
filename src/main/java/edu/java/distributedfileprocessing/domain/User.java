package edu.java.distributedfileprocessing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Объект пользовательских данных и сущность БД.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@ToString
public class User {

    /**
     * Идентификатор пользователя в БД
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    private UUID id;

    /**
     * Email
     */
    @Column(unique = true)
    @Setter
    @NotNull @Email
    private String email;

    /**
     * Идентификатор в пользователя в SecurityContext
     */
    @Setter
    @NotBlank
    private String sub;

}
