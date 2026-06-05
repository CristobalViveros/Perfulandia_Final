package com.example.pago_ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PagoRequestDTO(

        @NotNull(message = "El pedidoId es obligatorio")
        Long pedidoId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
        String estado
) {}