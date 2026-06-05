
package com.example.producto_ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.producto_ms.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Producto> findByEstadoIgnoreCase(String estado);

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByProveedorId(Long proveedorId);
}
