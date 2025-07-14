package com.mobileapp.repository;

import com.mobileapp.entity.Tranzactie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranzactieRepository extends JpaRepository<Tranzactie, Long> {
    List<Tranzactie> findByUsername(String username);
    Optional<Tranzactie> findByIdFactura(Long idFactura);
    List<Tranzactie> findByUsernameOrderByDateDesc(String username);
} 