package com.example.proveedores_ms.dto;

public record ProveedorResponseDTO(
        Long id,
        String nombre,
        String rut,
        String telefono,
        String email,
        String direccion,
        String estado,
        Long version
) {}
