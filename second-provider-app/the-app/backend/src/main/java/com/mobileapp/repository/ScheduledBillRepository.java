package com.mobileapp.repository;

import com.mobileapp.model.ScheduledBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledBillRepository extends JpaRepository<ScheduledBill, Long> {
    List<ScheduledBill> findByStatusAndScheduledDateTimeBefore(String status, LocalDateTime dateTime);
} 