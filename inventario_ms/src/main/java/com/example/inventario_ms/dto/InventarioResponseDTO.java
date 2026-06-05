package com.example.inventario_ms.dto;

import java.time.LocalDateTime;

public record InventarioResponseDTO(
        Long id,
        Long productoId,
        Integer stockActual,
        Integer stockMinimo,
        String ubicacion,
        String estado,
        LocalDateTime fechaActualizacion
) {}