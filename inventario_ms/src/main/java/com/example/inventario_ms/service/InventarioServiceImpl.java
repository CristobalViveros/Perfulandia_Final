package com.example.inventario_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventario_ms.dto.InventarioRequestDTO;
import com.example.inventario_ms.dto.InventarioResponseDTO;
import com.example.inventario_ms.exception.BadRequestException;
import com.example.inventario_ms.exception.DuplicateResourceException;
import com.example.inventario_ms.exception.ResourceNotFoundException;
import com.example.inventario_ms.model.Inventario;
import com.example.inventario_ms.repository.InventarioRepository;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {

    private static final Logger logger = LoggerFactory.getLogger(InventarioServiceImpl.class);

    private final InventarioRepository inventarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public InventarioResponseDTO crearInventario(InventarioRequestDTO dto) {
        logger.info("Iniciando creación de inventario para productoId={}", dto.productoId());

        validarStock(dto.stockActual(), dto.stockMinimo());

        if (inventarioRepository.existsByProductoId(dto.productoId())) {
            logger.warn("Intento de crear inventario duplicado para productoId={}", dto.productoId());
            throw new DuplicateResourceException("Ya existe inventario para ese producto");
        }

        Inventario inventario = new Inventario();
        inventario.setProductoId(dto.productoId());
        inventario.setStockActual(dto.stockActual());
        inventario.setStockMinimo(dto.stockMinimo());
        inventario.setUbicacion(dto.ubicacion());
        inventario.setEstado(dto.estado().trim().toUpperCase());
        inventario.setFechaActualizacion(LocalDateTime.now());

        Inventario guardado = inventarioRepository.save(inventario);

        logger.info("Inventario creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarInventario() {
        logger.info("Listando todo el inventario");

        return inventarioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerInventarioPorId(Long id) {
        logger.info("Buscando inventario por id={}", id);

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Inventario no encontrado con id={}", id);
                    return new ResourceNotFoundException("Inventario no encontrado con id: " + id);
                });

        return mapToResponseDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerInventarioPorProducto(Long productoId) {
        logger.info("Buscando inventario por productoId={}", productoId);

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> {
                    logger.warn("Inventario no encontrado para productoId={}", productoId);
                    return new ResourceNotFoundException("Inventario no encontrado para productoId: " + productoId);
                });

        return mapToResponseDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando inventario por estado={}", estado);

        return inventarioRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public InventarioResponseDTO actualizarInventario(Long id, InventarioRequestDTO dto) {
        logger.info("Iniciando actualización de inventario id={}", id);

        validarStock(dto.stockActual(), dto.stockMinimo());

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Inventario no encontrado con id={}", id);
                    return new ResourceNotFoundException("Inventario no encontrado con id: " + id);
                });

        inventarioRepository.findByProductoId(dto.productoId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar inventario id={} con productoId ya usado={}", id, dto.productoId());
                throw new DuplicateResourceException("Ya existe otro inventario asociado a ese producto");
            }
        });

        inventario.setProductoId(dto.productoId());
        inventario.setStockActual(dto.stockActual());
        inventario.setStockMinimo(dto.stockMinimo());
        inventario.setUbicacion(dto.ubicacion());
        inventario.setEstado(dto.estado().trim().toUpperCase());
        inventario.setFechaActualizacion(LocalDateTime.now());

        Inventario actualizado = inventarioRepository.save(inventario);

        logger.info("Inventario actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarInventario(Long id) {
        logger.info("Intentando eliminar inventario id={}", id);

        if (!inventarioRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Inventario no encontrado con id={}", id);
            throw new ResourceNotFoundException("Inventario no encontrado con id: " + id);
        }

        inventarioRepository.deleteById(id);

        logger.info("Inventario eliminado correctamente con id={}", id);
    }

    private void validarStock(Integer stockActual, Integer stockMinimo) {
        if (stockActual == null || stockActual < 0) {
            logger.warn("Stock actual inválido={}", stockActual);
            throw new BadRequestException("El stock actual no puede ser negativo");
        }

        if (stockMinimo != null && stockMinimo < 0) {
            logger.warn("Stock mínimo inválido={}", stockMinimo);
            throw new BadRequestException("El stock mínimo no puede ser negativo");
        }
    }

    private InventarioResponseDTO mapToResponseDTO(Inventario inventario) {
        return new InventarioResponseDTO(
                inventario.getId(),
                inventario.getProductoId(),
                inventario.getStockActual(),
                inventario.getStockMinimo(),
                inventario.getUbicacion(),
                inventario.getEstado(),
                inventario.getFechaActualizacion()
        );
    }
}