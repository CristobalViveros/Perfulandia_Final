
package com.example.producto_ms.clientdto;

public record ProveedorClientDTO(
        Long id,
        String nombre,
        String rut,
        String telefono,
        String email,
        String direccion,
        String estado,
        Long version
) {}
