package com.example.pedido_ms.clientdto;

import java.time.LocalDateTime;

public record InventarioClientDTO(
        Long id,
        Long productoId,
        Integer stockActual,
        Integer stockMinimo,
        String ubicacion,
        String estado,
        LocalDateTime fechaActualizacion,
        Long version
) {}
