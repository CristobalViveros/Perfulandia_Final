package com.example.categoria_ms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.categoria_ms.dto.CategoriaRequestDTO;
import com.example.categoria_ms.dto.CategoriaResponseDTO;
import com.example.categoria_ms.exception.DuplicateResourceException;
import com.example.categoria_ms.exception.ResourceNotFoundException;
import com.example.categoria_ms.model.Categoria;
import com.example.categoria_ms.repository.CategoriaRepository;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        logger.info("Iniciando creación de categoría con nombre={}", dto.nombre());

        String nombreNormalizado = dto.nombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            logger.warn("Intento de crear categoría duplicada con nombre={}", nombreNormalizado);
            throw new DuplicateResourceException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombreNormalizado);
        categoria.setDescripcion(dto.descripcion());
        categoria.setEstado(dto.estado().trim().toUpperCase());

        Categoria guardada = categoriaRepository.save(categoria);

        logger.info("Categoría creada correctamente con id={}", guardada.getId());

        return mapToResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        logger.info("Listando todas las categorías");

        return categoriaRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        logger.info("Buscando categoría por id={}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Categoría no encontrada con id={}", id);
                    return new ResourceNotFoundException("Categoría no encontrada con id: " + id);
                });

        return mapToResponseDTO(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando categorías por estado={}", estado);

        return categoriaRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto) {
        logger.info("Iniciando actualización de categoría id={}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Categoría no encontrada con id={}", id);
                    return new ResourceNotFoundException("Categoría no encontrada con id: " + id);
                });

        String nuevoNombre = dto.nombre().trim();

        categoriaRepository.findByNombreIgnoreCase(nuevoNombre).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar categoría id={} con nombre ya usado={}", id, nuevoNombre);
                throw new DuplicateResourceException("Ya existe otra categoría con ese nombre");
            }
        });

        categoria.setNombre(nuevoNombre);
        categoria.setDescripcion(dto.descripcion());
        categoria.setEstado(dto.estado().trim().toUpperCase());

        Categoria actualizada = categoriaRepository.save(categoria);

        logger.info("Categoría actualizada correctamente con id={}", actualizada.getId());

        return mapToResponseDTO(actualizada);
    }

    @Override
    public void eliminarCategoria(Long id) {
        logger.info("Intentando eliminar categoría id={}", id);

        if (!categoriaRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Categoría no encontrada con id={}", id);
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        }

        categoriaRepository.deleteById(id);

        logger.info("Categoría eliminada correctamente con id={}", id);
    }

    private CategoriaResponseDTO mapToResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado()
        );
    }
}