package org.task.webapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.task.webapplication.entity.User;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


}
