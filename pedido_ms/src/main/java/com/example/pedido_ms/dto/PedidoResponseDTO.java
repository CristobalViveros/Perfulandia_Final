package com.example.pedido_ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long clienteId,
        LocalDateTime fecha,
        String estado,
        BigDecimal total,
        List<DetallePedidoResponseDTO> detalles
) {}