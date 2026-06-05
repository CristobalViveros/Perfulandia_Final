package com.example.pedido_ms.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PedidoRequestDTO(

        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId,

        @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
        String estado,

        @NotEmpty(message = "El pedido debe tener al menos un detalle")
        List<@Valid DetallePedidoRequestDTO> detalles
) {}