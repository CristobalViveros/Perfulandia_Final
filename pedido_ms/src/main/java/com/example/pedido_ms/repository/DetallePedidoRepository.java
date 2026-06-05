package com.example.pedido_ms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pedido_ms.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByProductoId(Long productoId);
}

