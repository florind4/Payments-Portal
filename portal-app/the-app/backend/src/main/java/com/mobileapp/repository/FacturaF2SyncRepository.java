package com.mobileapp.repository;

import com.mobileapp.entity.FacturaF2Sync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacturaF2SyncRepository extends JpaRepository<FacturaF2Sync, Long> {
    
    @Query("SELECT f FROM FacturaF2Sync f WHERE " +
           "f.tip = :tip AND " +
           "f.sum = :sum AND " +
           "f.phone = :phone AND " +
           "f.furnizor = :furnizor")
    List<FacturaF2Sync> findMatchingUnpaidBills(
        @Param("tip") String tip,
        @Param("sum") Double sum,
        @Param("phone") String phone,
        @Param("furnizor") String furnizor
    );

    List<FacturaF2Sync> findByPlatita(String platita);
} 