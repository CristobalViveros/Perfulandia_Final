package com.example.pedido_ms.dto;

import java.math.BigDecimal;

public record DetallePedidoResponseDTO(
        Long id,
        Long productoId,
        Integer cantidad,
        BigDecimal precioUnitario
) {}