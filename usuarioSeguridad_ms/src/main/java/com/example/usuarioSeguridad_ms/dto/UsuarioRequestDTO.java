package com.example.usuarioSeguridad_ms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @NotBlank(message = "El username es obligatorio")
        @Size(max = 80, message = "El username no puede superar los 80 caracteres")
        String username,

        @NotBlank(message = "La password es obligatoria")
        @Size(min = 4, max = 100, message = "La password debe tener entre 4 y 100 caracteres")
        String password,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email,

        @NotBlank(message = "El rol es obligatorio")
        @Size(max = 50, message = "El rol no puede superar los 50 caracteres")
        String rol,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
        String estado
) {}