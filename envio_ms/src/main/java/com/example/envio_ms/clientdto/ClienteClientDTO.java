package com.example.envio_ms.clientdto;

public record ClienteClientDTO(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        Boolean activo,
        Long version
) {}