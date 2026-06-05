package com.example.envio_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.envio_ms.model.Envio;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByPedidoId(Long pedidoId);

    boolean existsByPedidoId(Long pedidoId);

    List<Envio> findByClienteId(Long clienteId);

    List<Envio> findByEstadoIgnoreCase(String estado);
}