package com.example.boletas_ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BoletaResponseDTO(
        Long id,
        Long pedidoId,
        Long clienteId,
        Long pagoId,
        BigDecimal total,
        String estado,
        LocalDateTime fechaEmision
) {}