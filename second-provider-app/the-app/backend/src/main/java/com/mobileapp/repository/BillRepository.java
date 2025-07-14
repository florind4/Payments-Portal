package com.mobileapp.repository;

import com.mobileapp.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByIsScheduledTrueAndStatusAndScheduledDateTimeLessThanEqual(
        String status, LocalDateTime scheduledDateTime);
    
    List<Bill> findByUsername(String username);
} 