package com.example.proveedores_ms.service;

import java.util.List;

import com.example.proveedores_ms.dto.ProveedorRequestDTO;
import com.example.proveedores_ms.dto.ProveedorResponseDTO;

public interface ProveedorService {

    ProveedorResponseDTO crearProveedor(ProveedorRequestDTO dto);

    List<ProveedorResponseDTO> listarProveedores();

    ProveedorResponseDTO obtenerProveedorPorId(Long id);

    List<ProveedorResponseDTO> listarPorEstado(String estado);

    ProveedorResponseDTO actualizarProveedor(Long id, ProveedorRequestDTO dto);

    void eliminarProveedor(Long id);
}