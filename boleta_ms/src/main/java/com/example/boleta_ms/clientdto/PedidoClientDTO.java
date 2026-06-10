package com.example.boleta_ms.clientdto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoClientDTO(
        Long id,
        Long clienteId,
        LocalDateTime fecha,
        String estado,
        BigDecimal total,
        List<Object> detalles
) {}