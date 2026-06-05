package com.example.proveedores_ms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proveedores_ms.dto.ProveedorRequestDTO;
import com.example.proveedores_ms.dto.ProveedorResponseDTO;
import com.example.proveedores_ms.exception.DuplicateResourceException;
import com.example.proveedores_ms.exception.ResourceNotFoundException;
import com.example.proveedores_ms.model.Proveedor;
import com.example.proveedores_ms.repository.ProveedorRepository;

@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorServiceImpl.class);

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public ProveedorResponseDTO crearProveedor(ProveedorRequestDTO dto) {
        logger.info("Iniciando creación de proveedor con rut={} email={}", dto.rut(), dto.email());

        String rutNormalizado = dto.rut().trim().toUpperCase();
        String emailNormalizado = dto.email().trim().toLowerCase();

        if (proveedorRepository.existsByRutIgnoreCase(rutNormalizado)) {
            logger.warn("Intento de crear proveedor duplicado con rut={}", rutNormalizado);
            throw new DuplicateResourceException("Ya existe un proveedor con ese RUT");
        }

        if (proveedorRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            logger.warn("Intento de crear proveedor con email duplicado={}", emailNormalizado);
            throw new DuplicateResourceException("Ya existe un proveedor con ese email");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.nombre().trim());
        proveedor.setRut(rutNormalizado);
        proveedor.setTelefono(dto.telefono());
        proveedor.setEmail(emailNormalizado);
        proveedor.setDireccion(dto.direccion());
        proveedor.setEstado(dto.estado().trim().toUpperCase());

        Proveedor guardado = proveedorRepository.save(proveedor);

        logger.info("Proveedor creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> listarProveedores() {
        logger.info("Listando todos los proveedores");

        return proveedorRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO obtenerProveedorPorId(Long id) {
        logger.info("Buscando proveedor por id={}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Proveedor no encontrado con id={}", id);
                    return new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
                });

        return mapToResponseDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando proveedores por estado={}", estado);

        return proveedorRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public ProveedorResponseDTO actualizarProveedor(Long id, ProveedorRequestDTO dto) {
        logger.info("Iniciando actualización de proveedor id={}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Proveedor no encontrado con id={}", id);
                    return new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
                });

        String nuevoRut = dto.rut().trim().toUpperCase();
        String nuevoEmail = dto.email().trim().toLowerCase();

        proveedorRepository.findByRutIgnoreCase(nuevoRut).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar proveedor id={} con rut ya usado={}", id, nuevoRut);
                throw new DuplicateResourceException("Ya existe otro proveedor con ese RUT");
            }
        });

        proveedorRepository.findByEmailIgnoreCase(nuevoEmail).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar proveedor id={} con email ya usado={}", id, nuevoEmail);
                throw new DuplicateResourceException("Ya existe otro proveedor con ese email");
            }
        });

        proveedor.setNombre(dto.nombre().trim());
        proveedor.setRut(nuevoRut);
        proveedor.setTelefono(dto.telefono());
        proveedor.setEmail(nuevoEmail);
        proveedor.setDireccion(dto.direccion());
        proveedor.setEstado(dto.estado().trim().toUpperCase());

        Proveedor actualizado = proveedorRepository.save(proveedor);

        logger.info("Proveedor actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarProveedor(Long id) {
        logger.info("Intentando eliminar proveedor id={}", id);

        if (!proveedorRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Proveedor no encontrado con id={}", id);
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
        }

        proveedorRepository.deleteById(id);

        logger.info("Proveedor eliminado correctamente con id={}", id);
    }

    private ProveedorResponseDTO mapToResponseDTO(Proveedor proveedor) {
        return new ProveedorResponseDTO(
                proveedor.getId(),
                proveedor.getNombre(),
                proveedor.getRut(),
                proveedor.getTelefono(),
                proveedor.getEmail(),
                proveedor.getDireccion(),
                proveedor.getEstado(),
                proveedor.getVersion()
        );
    }
}
