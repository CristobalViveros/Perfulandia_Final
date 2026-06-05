package com.example.pago_ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponseDTO(
        Long id,
        Long pedidoId,
        BigDecimal monto,
        String estado,
        LocalDateTime fecha
) {}