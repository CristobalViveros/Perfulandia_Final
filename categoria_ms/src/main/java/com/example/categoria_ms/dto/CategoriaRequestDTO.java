package com.example.categoria_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
        String descripcion,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
        String estado
) {}