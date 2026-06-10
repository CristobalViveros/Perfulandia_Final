package com.example.boleta_ms.clientdto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoClientDTO(
        Long id,
        Long pedidoId,
        BigDecimal monto,
        String estado,
        LocalDateTime fecha
) {}