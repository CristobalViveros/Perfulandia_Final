package com.example.inventario_ms.service;

import java.util.List;

import com.example.inventario_ms.dto.InventarioRequestDTO;
import com.example.inventario_ms.dto.InventarioResponseDTO;

public interface InventarioService {

    InventarioResponseDTO crearInventario(InventarioRequestDTO dto);

    List<InventarioResponseDTO> listarInventario();

    InventarioResponseDTO obtenerInventarioPorId(Long id);

    InventarioResponseDTO obtenerInventarioPorProducto(Long productoId);

    List<InventarioResponseDTO> listarPorEstado(String estado);

    InventarioResponseDTO actualizarInventario(Long id, InventarioRequestDTO dto);

    void eliminarInventario(Long id);
}
