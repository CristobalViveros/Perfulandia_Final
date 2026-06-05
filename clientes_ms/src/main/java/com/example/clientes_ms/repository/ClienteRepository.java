package com.example.clientes_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clientes_ms.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Cliente> findByActivo(Boolean activo);
}
