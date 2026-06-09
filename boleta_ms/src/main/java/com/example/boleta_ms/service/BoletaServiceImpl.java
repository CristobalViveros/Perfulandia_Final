package com.example.boleta_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boleta_ms.dto.BoletaRequestDTO;
import com.example.boleta_ms.dto.BoletaResponseDTO;
import com.example.boleta_ms.exception.BadRequestException;
import com.example.boleta_ms.exception.DuplicateResourceException;
import com.example.boleta_ms.exception.ResourceNotFoundException;
import com.example.boleta_ms.model.Boleta;
import com.example.boleta_ms.repository.BoletaRepository;

@Service
@Transactional
public class BoletaServiceImpl implements BoletaService {

    private static final Logger logger = LoggerFactory.getLogger(BoletaServiceImpl.class);

    private final BoletaRepository boletaRepository;

    public BoletaServiceImpl(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @Override
    public BoletaResponseDTO crearBoleta(BoletaRequestDTO dto) {
        logger.info("Iniciando creación de boleta para pedidoId={}, clienteId={}, pagoId={}",
                dto.pedidoId(), dto.clienteId(), dto.pagoId());

        validarTotal(dto);

        if (boletaRepository.existsByPagoId(dto.pagoId())) {
            logger.warn("Intento de crear boleta duplicada para pagoId={}", dto.pagoId());
            throw new DuplicateResourceException("Ya existe una boleta asociada a ese pago");
        }

        Boleta boleta = new Boleta();
        boleta.setPedidoId(dto.pedidoId());
        boleta.setClienteId(dto.clienteId());
        boleta.setPagoId(dto.pagoId());
        boleta.setTotal(dto.total());
        boleta.setEstado(dto.estado().trim().toUpperCase());
        boleta.setFechaEmision(LocalDateTime.now());

        Boleta guardada = boletaRepository.save(boleta);

        logger.info("Boleta creada correctamente con id={}", guardada.getId());

        return mapToResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> listarBoletas() {
        logger.info("Listando todas las boletas");

        return boletaRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BoletaResponseDTO obtenerBoletaPorId(Long id) {
        logger.info("Buscando boleta por id={}", id);

        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Boleta no encontrada con id={}", id);
                    return new ResourceNotFoundException("Boleta no encontrada con id: " + id);
                });

        return mapToResponseDTO(boleta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> obtenerBoletasPorCliente(Long clienteId) {
        logger.info("Buscando boletas por clienteId={}", clienteId);

        return boletaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BoletaResponseDTO obtenerBoletaPorPedido(Long pedidoId) {
        logger.info("Buscando boleta por pedidoId={}", pedidoId);

        Boleta boleta = boletaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    logger.warn("Boleta no encontrada para pedidoId={}", pedidoId);
                    return new ResourceNotFoundException("No se encontró boleta para el pedido con id: " + pedidoId);
                });

        return mapToResponseDTO(boleta);
    }

    @Override
    @Transactional(readOnly = true)
    public BoletaResponseDTO obtenerBoletaPorPago(Long pagoId) {
        logger.info("Buscando boleta por pagoId={}", pagoId);

        Boleta boleta = boletaRepository.findByPagoId(pagoId)
                .orElseThrow(() -> {
                    logger.warn("Boleta no encontrada para pagoId={}", pagoId);
                    return new ResourceNotFoundException("No se encontró boleta para el pago con id: " + pagoId);
                });

        return mapToResponseDTO(boleta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando boletas por estado={}", estado);

        return boletaRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public BoletaResponseDTO actualizarBoleta(Long id, BoletaRequestDTO dto) {
        logger.info("Iniciando actualización de boleta id={}", id);

        validarTotal(dto);

        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Boleta no encontrada con id={}", id);
                    return new ResourceNotFoundException("Boleta no encontrada con id: " + id);
                });

        boletaRepository.findByPagoId(dto.pagoId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar boleta id={} con pagoId ya usado={}", id, dto.pagoId());
                throw new DuplicateResourceException("Ya existe otra boleta asociada a ese pago");
            }
        });

        boleta.setPedidoId(dto.pedidoId());
        boleta.setClienteId(dto.clienteId());
        boleta.setPagoId(dto.pagoId());
        boleta.setTotal(dto.total());
        boleta.setEstado(dto.estado().trim().toUpperCase());

        /*
         * No modificamos fechaEmision porque representa cuándo fue emitida la boleta.
         * Si más adelante necesitas trazabilidad de cambios, puedes agregar fechaActualizacion.
         */

        Boleta actualizada = boletaRepository.save(boleta);

        logger.info("Boleta actualizada correctamente con id={}", actualizada.getId());

        return mapToResponseDTO(actualizada);
    }

    @Override
    public void eliminarBoleta(Long id) {
        logger.info("Intentando eliminar boleta id={}", id);

        if (!boletaRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Boleta no encontrada con id={}", id);
            throw new ResourceNotFoundException("Boleta no encontrada con id: " + id);
        }

        boletaRepository.deleteById(id);

        logger.info("Boleta eliminada correctamente con id={}", id);
    }

    private void validarTotal(BoletaRequestDTO dto) {
        if (dto.total() == null || dto.total().signum() <= 0) {
            logger.warn("Total inválido recibido={}", dto.total());
            throw new BadRequestException("El total debe ser mayor a cero");
        }
    }

    private BoletaResponseDTO mapToResponseDTO(Boleta boleta) {
        return new BoletaResponseDTO(
                boleta.getId(),
                boleta.getPedidoId(),
                boleta.getClienteId(),
                boleta.getPagoId(),
                boleta.getTotal(),
                boleta.getEstado(),
                boleta.getFechaEmision()
        );
    }
}