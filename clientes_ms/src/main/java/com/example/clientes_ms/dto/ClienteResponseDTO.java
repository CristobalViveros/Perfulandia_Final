
package com.example.clientes_ms.dto;

public record ClienteResponseDTO(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        Boolean activo,
        Long version
) {}
