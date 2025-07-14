package com.mobileapp.repository;

import com.mobileapp.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUsername(String username);
    List<Bill> findByUsernameAndPlatita(String username, String platita);
    List<Bill> findByPlatita(String platita);

    @Query("SELECT b FROM Bill b WHERE b.sum <= :maxPrice")
    List<Bill> findByMaxPrice(@Param("maxPrice") Double maxPrice);

    @Query("SELECT b FROM Bill b WHERE b.furnizor = :provider")
    List<Bill> findByProvider(@Param("provider") String provider);

    @Query("SELECT b FROM Bill b WHERE b.sum <= :maxPrice AND b.furnizor = :provider")
    List<Bill> findByMaxPriceAndProvider(@Param("maxPrice") Double maxPrice, @Param("provider") String provider);

    @Query("SELECT b FROM Bill b WHERE b.username = :username AND b.sum <= :maxPrice")
    List<Bill> findByUsernameAndMaxPrice(@Param("username") String username, @Param("maxPrice") Double maxPrice);

    @Query("SELECT b FROM Bill b WHERE b.username = :username AND b.furnizor = :provider")
    List<Bill> findByUsernameAndProvider(@Param("username") String username, @Param("provider") String provider);

    @Query("SELECT b FROM Bill b WHERE b.username = :username AND b.sum <= :maxPrice AND b.furnizor = :provider")
    List<Bill> findByUsernameAndMaxPriceAndProvider(@Param("username") String username, @Param("maxPrice") Double maxPrice, @Param("provider") String provider);
} 