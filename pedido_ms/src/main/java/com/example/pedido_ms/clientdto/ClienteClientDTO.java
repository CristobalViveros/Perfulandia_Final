
package com.example.pedido_ms.clientdto;

public record ClienteClientDTO(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        Boolean activo,
        Long version
) {}

