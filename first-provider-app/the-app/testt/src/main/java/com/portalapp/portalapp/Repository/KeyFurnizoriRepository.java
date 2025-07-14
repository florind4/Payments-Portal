package com.portalapp.portalapp.Repository;

import com.portalapp.portalapp.Model.KeyFurnizori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeyFurnizoriRepository extends JpaRepository<KeyFurnizori, Long> {
    @Query("SELECT k.secret FROM KeyFurnizori k WHERE k.nume = :nume")
    String findSecretByNume(@Param("nume") String nume);
}
