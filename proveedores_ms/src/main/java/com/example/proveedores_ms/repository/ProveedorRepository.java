package com.example.proveedores_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.proveedores_ms.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRutIgnoreCase(String rut);

    Optional<Proveedor> findByEmailIgnoreCase(String email);

    boolean existsByRutIgnoreCase(String rut);

    boolean existsByEmailIgnoreCase(String email);

    List<Proveedor> findByEstadoIgnoreCase(String estado);
}