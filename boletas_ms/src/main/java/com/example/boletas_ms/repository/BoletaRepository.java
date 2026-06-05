package com.example.boletas_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boletas_ms.model.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findByClienteId(Long clienteId);

    Optional<Boleta> findByPedidoId(Long pedidoId);

    Optional<Boleta> findByPagoId(Long pagoId);

    List<Boleta> findByEstado(String estado);

    boolean existsByPagoId(Long pagoId);
}