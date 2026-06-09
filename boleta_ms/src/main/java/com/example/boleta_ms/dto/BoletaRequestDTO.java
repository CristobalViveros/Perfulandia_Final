package com.example.boleta_ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BoletaRequestDTO(

        @NotNull(message = "El pedidoId es obligatorio")
        Long pedidoId,

        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId,

        @NotNull(message = "El pagoId es obligatorio")
        Long pagoId,

        @NotNull(message = "El total es obligatorio")
        @Positive(message = "El total debe ser mayor a 0")
        BigDecimal total,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
        String estado
) {}

