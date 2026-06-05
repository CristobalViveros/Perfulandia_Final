package com.example.usuarioSeguridad_ms.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String username,
        String email,
        String rol,
        String estado,
        LocalDateTime fechaCreacion
) {}