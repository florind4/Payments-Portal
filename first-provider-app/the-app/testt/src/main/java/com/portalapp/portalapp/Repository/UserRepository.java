package com.portalapp.portalapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portalapp.portalapp.Model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
