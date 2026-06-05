package com.example.inventario_ms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InventarioRequestDTO(

        @NotNull(message = "El productoId es obligatorio")
        Long productoId,

        @NotNull(message = "El stockActual es obligatorio")
        @Min(value = 0, message = "El stock actual no puede ser negativo")
        Integer stockActual,

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo,

        @Size(max = 100, message = "La ubicación no puede superar los 100 caracteres")
        String ubicacion,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
        String estado
) {}