
package com.example.producto_ms.service;

import java.util.List;

import com.example.producto_ms.dto.ProductoRequestDTO;
import com.example.producto_ms.dto.ProductoResponseDTO;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO dto);

    List<ProductoResponseDTO> listarProductos();

    ProductoResponseDTO obtenerProductoPorId(Long id);

    List<ProductoResponseDTO> listarPorEstado(String estado);

    List<ProductoResponseDTO> listarPorCategoria(Long categoriaId);

    List<ProductoResponseDTO> listarPorProveedor(Long proveedorId);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto);

    void eliminarProducto(Long id);
}