package com.example.pedido_ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetallePedidoRequestDTO(

        @NotNull(message = "El productoId es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotNull(message = "El precioUnitario es obligatorio")
        @Positive(message = "El precio unitario debe ser mayor a 0")
        BigDecimal precioUnitario
) {}