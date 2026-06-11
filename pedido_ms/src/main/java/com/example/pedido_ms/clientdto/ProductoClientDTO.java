package com.example.pedido_ms.clientdto;

import java.math.BigDecimal;

public record ProductoClientDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String marca,
        String estado,
        Long categoriaId,
        Long proveedorId
) {}