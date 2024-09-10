package edu.java.distributedfileprocessing.repository;

import edu.java.distributedfileprocessing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для данных пользователей.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String username);
}
