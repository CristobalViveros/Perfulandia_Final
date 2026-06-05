package com.example.producto_ms.dto;

import java.math.BigDecimal;

public record ProductoResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String marca,
        String estado,
        Long categoriaId,
        Long proveedorId
) {}