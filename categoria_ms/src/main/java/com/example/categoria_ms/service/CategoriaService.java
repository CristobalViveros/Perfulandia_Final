package com.example.categoria_ms.service;

import java.util.List;

import com.example.categoria_ms.dto.CategoriaRequestDTO;
import com.example.categoria_ms.dto.CategoriaResponseDTO;

public interface CategoriaService {

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto);

    List<CategoriaResponseDTO> listarCategorias();

    CategoriaResponseDTO obtenerCategoriaPorId(Long id);

    List<CategoriaResponseDTO> listarPorEstado(String estado);

    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto);

    void eliminarCategoria(Long id);
}