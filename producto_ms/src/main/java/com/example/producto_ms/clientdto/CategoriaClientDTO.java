package com.example.producto_ms.clientdto;

public record CategoriaClientDTO(
        Long id,
        String nombre,
        String descripcion,
        String estado
) {}