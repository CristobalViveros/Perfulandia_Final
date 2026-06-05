package com.example.pago_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pago_ms.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedidoId(Long pedidoId);

    boolean existsByPedidoId(Long pedidoId);

    List<Pago> findByEstadoIgnoreCase(String estado);
}