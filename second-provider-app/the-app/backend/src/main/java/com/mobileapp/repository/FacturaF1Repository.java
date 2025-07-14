package com.mobileapp.repository;

import com.mobileapp.entity.FacturaF1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacturaF1Repository extends JpaRepository<FacturaF1, Long> {
    List<FacturaF1> findByUsername(String username);
} 