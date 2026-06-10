package com.example.boleta_ms.clientdto;

public record ClienteClientDTO(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        Boolean activo,
        Long version
) {}
