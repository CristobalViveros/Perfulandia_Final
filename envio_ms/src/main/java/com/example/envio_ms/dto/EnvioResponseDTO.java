package com.example.envio_ms.dto;

import java.time.LocalDateTime;

public record EnvioResponseDTO(
        Long id,
        Long pedidoId,
        Long clienteId,
        String direccionEntrega,
        String comuna,
        String ciudad,
        String ubicacionActual,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime ultimaActualizacion,
        Long version
) {}