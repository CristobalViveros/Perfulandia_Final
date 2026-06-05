package com.example.envio_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.envio_ms.dto.EnvioRequestDTO;
import com.example.envio_ms.dto.EnvioResponseDTO;
import com.example.envio_ms.exception.DuplicateResourceException;
import com.example.envio_ms.exception.ResourceNotFoundException;
import com.example.envio_ms.model.Envio;
import com.example.envio_ms.repository.EnvioRepository;

@Service
@Transactional
public class EnvioServiceImpl implements EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioServiceImpl.class);

    private final EnvioRepository envioRepository;

    public EnvioServiceImpl(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    public EnvioResponseDTO crearEnvio(EnvioRequestDTO dto) {
        logger.info("Iniciando creación de envío para pedidoId={} y clienteId={}", dto.pedidoId(), dto.clienteId());

        if (envioRepository.existsByPedidoId(dto.pedidoId())) {
            logger.warn("Intento de crear envío duplicado para pedidoId={}", dto.pedidoId());
            throw new DuplicateResourceException("Ya existe un envío asociado a ese pedido");
        }

        LocalDateTime ahora = LocalDateTime.now();

        Envio envio = new Envio();
        envio.setPedidoId(dto.pedidoId());
        envio.setClienteId(dto.clienteId());
        envio.setDireccionEntrega(dto.direccionEntrega().trim());
        envio.setComuna(dto.comuna().trim());
        envio.setCiudad(dto.ciudad().trim());
        envio.setUbicacionActual(dto.ubicacionActual());
        envio.setEstado(dto.estado().trim().toUpperCase());
        envio.setFechaCreacion(ahora);
        envio.setUltimaActualizacion(ahora);

        Envio guardado = envioRepository.save(envio);

        logger.info("Envío creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarEnvios() {
        logger.info("Listando todos los envíos");

        return envioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerEnvioPorId(Long id) {
        logger.info("Buscando envío por id={}", id);

        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Envío no encontrado con id={}", id);
                    return new ResourceNotFoundException("Envío no encontrado con id: " + id);
                });

        return mapToResponseDTO(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerEnvioPorPedido(Long pedidoId) {
        logger.info("Buscando envío por pedidoId={}", pedidoId);

        Envio envio = envioRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    logger.warn("Envío no encontrado para pedidoId={}", pedidoId);
                    return new ResourceNotFoundException("Envío no encontrado para pedidoId: " + pedidoId);
                });

        return mapToResponseDTO(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarPorCliente(Long clienteId) {
        logger.info("Listando envíos por clienteId={}", clienteId);

        return envioRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando envíos por estado={}", estado);

        return envioRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public EnvioResponseDTO actualizarEnvio(Long id, EnvioRequestDTO dto) {
        logger.info("Iniciando actualización de envío id={}", id);

        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Envío no encontrado con id={}", id);
                    return new ResourceNotFoundException("Envío no encontrado con id: " + id);
                });

        envioRepository.findByPedidoId(dto.pedidoId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar envío id={} con pedidoId ya usado={}", id, dto.pedidoId());
                throw new DuplicateResourceException("Ya existe otro envío asociado a ese pedido");
            }
        });

        envio.setPedidoId(dto.pedidoId());
        envio.setClienteId(dto.clienteId());
        envio.setDireccionEntrega(dto.direccionEntrega().trim());
        envio.setComuna(dto.comuna().trim());
        envio.setCiudad(dto.ciudad().trim());
        envio.setUbicacionActual(dto.ubicacionActual());
        envio.setEstado(dto.estado().trim().toUpperCase());
        envio.setUltimaActualizacion(LocalDateTime.now());

        Envio actualizado = envioRepository.save(envio);

        logger.info("Envío actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public EnvioResponseDTO actualizarEstado(Long id, String estado) {
        logger.info("Actualizando estado de envío id={} a estado={}", id, estado);

        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar estado. Envío no encontrado con id={}", id);
                    return new ResourceNotFoundException("Envío no encontrado con id: " + id);
                });

        envio.setEstado(estado.trim().toUpperCase());
        envio.setUltimaActualizacion(LocalDateTime.now());

        Envio actualizado = envioRepository.save(envio);

        logger.info("Estado de envío actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarEnvio(Long id) {
        logger.info("Intentando eliminar envío id={}", id);

        if (!envioRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Envío no encontrado con id={}", id);
            throw new ResourceNotFoundException("Envío no encontrado con id: " + id);
        }

        envioRepository.deleteById(id);

        logger.info("Envío eliminado correctamente con id={}", id);
    }

    private EnvioResponseDTO mapToResponseDTO(Envio envio) {
        return new EnvioResponseDTO(
                envio.getId(),
                envio.getPedidoId(),
                envio.getClienteId(),
                envio.getDireccionEntrega(),
                envio.getComuna(),
                envio.getCiudad(),
                envio.getUbicacionActual(),
                envio.getEstado(),
                envio.getFechaCreacion(),
                envio.getUltimaActualizacion(),
                envio.getVersion()
        );
    }
}