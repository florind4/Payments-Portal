package com.mobileapp.repository;

import com.mobileapp.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByUsername(String username);
    Connection findByUsernameAndFurnizor(String username, String furnizor);
} 