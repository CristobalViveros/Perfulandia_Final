package com.example.producto_ms.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.producto_ms.dto.ProductoRequestDTO;
import com.example.producto_ms.dto.ProductoResponseDTO;
import com.example.producto_ms.exception.BadRequestException;
import com.example.producto_ms.exception.DuplicateResourceException;
import com.example.producto_ms.exception.ResourceNotFoundException;
import com.example.producto_ms.model.Producto;
import com.example.producto_ms.repository.ProductoRepository;


import com.example.producto_ms.client.CategoriaClient;
import com.example.producto_ms.client.ProveedorClient;
import com.example.producto_ms.clientdto.CategoriaClientDTO;
import com.example.producto_ms.clientdto.ProveedorClientDTO;

import feign.FeignException;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;
    private final CategoriaClient categoriaClient;
    private final ProveedorClient proveedorClient;


    
    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaClient categoriaClient,
            ProveedorClient proveedorClient) {
        this.productoRepository = productoRepository;
        this.categoriaClient = categoriaClient;
        this.proveedorClient = proveedorClient;
    }

    @Override
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        logger.info("Iniciando creación de producto con nombre={}", dto.nombre());

        validarPrecio(dto.precio());

        String nombreNormalizado = dto.nombre().trim();

        if (productoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            logger.warn("Intento de crear producto duplicado con nombre={}", nombreNormalizado);
            throw new DuplicateResourceException("Ya existe un producto con ese nombre");
        }

        validarReferenciasExternas(dto);

        Producto producto = new Producto();
        producto.setNombre(nombreNormalizado);
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setMarca(dto.marca());
        producto.setEstado(dto.estado().trim().toUpperCase());
        producto.setCategoriaId(dto.categoriaId());
        producto.setProveedorId(dto.proveedorId());

        Producto guardado = productoRepository.save(producto);

        logger.info("Producto creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos() {
        logger.info("Listando todos los productos");

        return productoRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        logger.info("Buscando producto por id={}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con id={}", id);
                    return new ResourceNotFoundException("Producto no encontrado con id: " + id);
                });

        return mapToResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando productos por estado={}", estado);

        return productoRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
        logger.info("Listando productos por categoriaId={}", categoriaId);

        return productoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorProveedor(Long proveedorId) {
        logger.info("Listando productos por proveedorId={}", proveedorId);

        return productoRepository.findByProveedorId(proveedorId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        logger.info("Iniciando actualización de producto id={}", id);

        validarPrecio(dto.precio());

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Producto no encontrado con id={}", id);
                    return new ResourceNotFoundException("Producto no encontrado con id: " + id);
                });

        String nuevoNombre = dto.nombre().trim();

        productoRepository.findByNombreIgnoreCase(nuevoNombre).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar producto id={} con nombre ya usado={}", id, nuevoNombre);
                throw new DuplicateResourceException("Ya existe otro producto con ese nombre");
            }
        });

        validarReferenciasExternas(dto);

        producto.setNombre(nuevoNombre);
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setMarca(dto.marca());
        producto.setEstado(dto.estado().trim().toUpperCase());
        producto.setCategoriaId(dto.categoriaId());
        producto.setProveedorId(dto.proveedorId());

        Producto actualizado = productoRepository.save(producto);

        logger.info("Producto actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarProducto(Long id) {
        logger.info("Intentando eliminar producto id={}", id);

        if (!productoRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Producto no encontrado con id={}", id);
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }

        productoRepository.deleteById(id);

        logger.info("Producto eliminado correctamente con id={}", id);
    }

    private void validarReferenciasExternas(ProductoRequestDTO dto) {
        try {
            CategoriaClientDTO categoria = categoriaClient.obtenerCategoriaPorId(dto.categoriaId());

            if (!esEstadoActivo(categoria.estado())) {
                throw new BadRequestException("La categoría asociada al producto no está activa");
            }

            if (dto.proveedorId() != null) {
                ProveedorClientDTO proveedor = proveedorClient.obtenerProveedorPorId(dto.proveedorId());

                if (!esEstadoActivo(proveedor.estado())) {
                    throw new BadRequestException("El proveedor asociado al producto no está activo");
                }
            }

        } catch (FeignException.NotFound ex) {
            logger.warn("Referencia externa no encontrada al crear/actualizar producto", ex);
            throw new ResourceNotFoundException("No se encontró categoría o proveedor asociado");
        } catch (FeignException.Unauthorized ex) {
            logger.warn("No autorizado al consultar otro microservicio", ex);
            throw new BadRequestException("No autorizado al consultar otro microservicio");
        } catch (FeignException ex) {
            logger.error("Error Feign al comunicarse con otro microservicio. Status={}", ex.status(), ex);
            throw new BadRequestException("Error al comunicarse con otro microservicio: " + ex.status());
        }
    }

    private boolean esEstadoActivo(String estado) {
        return estado != null &&
                ("ACTIVO".equalsIgnoreCase(estado) || "ACTIVA".equalsIgnoreCase(estado));
    }

    private void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() <= 0) {
            logger.warn("Precio inválido recibido={}", precio);
            throw new BadRequestException("El precio debe ser mayor a cero");
        }
    }

    private ProductoResponseDTO mapToResponseDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getMarca(),
                producto.getEstado(),
                producto.getCategoriaId(),
                producto.getProveedorId()
        );
    }
}