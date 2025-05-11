package com.example.odontofast.repository;

import com.example.odontofast.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    Optional<Dentista> findByCroAndSenhaDentista(String cro, String senhaDentista);
    Optional<Dentista> findByCro(String cro);
}