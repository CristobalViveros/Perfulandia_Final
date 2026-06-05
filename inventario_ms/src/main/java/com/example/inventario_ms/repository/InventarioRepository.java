package com.example.inventario_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventario_ms.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);

    List<Inventario> findByEstadoIgnoreCase(String estado);
}
