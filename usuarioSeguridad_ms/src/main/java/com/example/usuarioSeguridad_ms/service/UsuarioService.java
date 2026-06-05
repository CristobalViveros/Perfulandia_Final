package com.example.usuarioSeguridad_ms.service;

import java.util.List;

import com.example.usuarioSeguridad_ms.dto.UsuarioRequestDTO;
import com.example.usuarioSeguridad_ms.dto.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuarioResponseDTO obtenerUsuarioPorId(Long id);

    UsuarioResponseDTO obtenerUsuarioPorUsername(String username);

    List<UsuarioResponseDTO> listarPorRol(String rol);

    List<UsuarioResponseDTO> listarPorEstado(String estado);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto);

    void eliminarUsuario(Long id);
}
