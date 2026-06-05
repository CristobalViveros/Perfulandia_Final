package com.example.envio_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnvioRequestDTO(

        @NotNull(message = "El pedidoId es obligatorio")
        Long pedidoId,

        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId,

        @NotBlank(message = "La dirección de entrega es obligatoria")
        @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
        String direccionEntrega,

        @NotBlank(message = "La comuna es obligatoria")
        @Size(max = 80, message = "La comuna no puede superar los 80 caracteres")
        String comuna,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 80, message = "La ciudad no puede superar los 80 caracteres")
        String ciudad,

        @Size(max = 150, message = "La ubicación actual no puede superar los 150 caracteres")
        String ubicacionActual,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
        String estado
) {}