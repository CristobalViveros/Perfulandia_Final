package com.example.clientes_ms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 120, message = "Los apellidos no pueden superar los 120 caracteres")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email,

        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
        String telefono,

        Boolean activo
) {}
