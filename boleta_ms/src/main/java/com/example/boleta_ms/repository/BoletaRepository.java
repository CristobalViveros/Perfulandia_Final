package com.example.boleta_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boleta_ms.model.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findByClienteId(Long clienteId);

    Optional<Boleta> findByPedidoId(Long pedidoId);

    Optional<Boleta> findByPagoId(Long pagoId);

    List<Boleta> findByEstadoIgnoreCase(String estado);

    boolean existsByPagoId(Long pagoId);
}