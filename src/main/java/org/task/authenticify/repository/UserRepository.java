package org.task.authenticify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.task.authenticify.entity.user.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);
}
