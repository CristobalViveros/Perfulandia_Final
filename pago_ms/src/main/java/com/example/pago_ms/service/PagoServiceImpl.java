package com.example.pago_ms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pago_ms.dto.PagoRequestDTO;
import com.example.pago_ms.dto.PagoResponseDTO;
import com.example.pago_ms.exception.BadRequestException;
import com.example.pago_ms.exception.DuplicateResourceException;
import com.example.pago_ms.exception.ResourceNotFoundException;
import com.example.pago_ms.model.Pago;
import com.example.pago_ms.repository.PagoRepository;

@Service
@Transactional
public class PagoServiceImpl implements PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final PagoRepository pagoRepository;

    public PagoServiceImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public PagoResponseDTO crearPago(PagoRequestDTO dto) {
        logger.info("Iniciando creación de pago para pedidoId={}", dto.pedidoId());

        validarMonto(dto.monto());

        if (pagoRepository.existsByPedidoId(dto.pedidoId())) {
            logger.warn("Intento de crear pago duplicado para pedidoId={}", dto.pedidoId());
            throw new DuplicateResourceException("Ya existe un pago asociado a ese pedido");
        }

        Pago pago = new Pago();
        pago.setPedidoId(dto.pedidoId());
        pago.setMonto(dto.monto());
        pago.setEstado(dto.estado().trim().toUpperCase());
        pago.setFecha(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);

        logger.info("Pago creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    public PagoResponseDTO procesarPago(PagoRequestDTO dto) {
        logger.info("Procesando pago para pedidoId={}", dto.pedidoId());

        validarMonto(dto.monto());

        if (pagoRepository.existsByPedidoId(dto.pedidoId())) {
            logger.warn("Intento de procesar pago duplicado para pedidoId={}", dto.pedidoId());
            throw new DuplicateResourceException("Ya existe un pago asociado a ese pedido");
        }

        Pago pago = new Pago();
        pago.setPedidoId(dto.pedidoId());
        pago.setMonto(dto.monto());
        pago.setFecha(LocalDateTime.now());

        /*
         * Lógica simple para evaluación:
         * Si el monto es mayor a 0, el pago se aprueba.
         * Más adelante esto podría conectarse con transacciones reales o pasarela de pago.
         */
        pago.setEstado("APROBADO");

        Pago guardado = pagoRepository.save(pago);

        logger.info("Pago procesado correctamente con id={} estado={}", guardado.getId(), guardado.getEstado());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPagos() {
        logger.info("Listando todos los pagos");

        return pagoRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPagoPorId(Long id) {
        logger.info("Buscando pago por id={}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pago no encontrado con id={}", id);
                    return new ResourceNotFoundException("Pago no encontrado con id: " + id);
                });

        return mapToResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPagoPorPedido(Long pedidoId) {
        logger.info("Buscando pago por pedidoId={}", pedidoId);

        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    logger.warn("Pago no encontrado para pedidoId={}", pedidoId);
                    return new ResourceNotFoundException("Pago no encontrado para pedidoId: " + pedidoId);
                });

        return mapToResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando pagos por estado={}", estado);

        return pagoRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public PagoResponseDTO actualizarPago(Long id, PagoRequestDTO dto) {
        logger.info("Iniciando actualización de pago id={}", id);

        validarMonto(dto.monto());

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Pago no encontrado con id={}", id);
                    return new ResourceNotFoundException("Pago no encontrado con id: " + id);
                });

        pagoRepository.findByPedidoId(dto.pedidoId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar pago id={} con pedidoId ya usado={}", id, dto.pedidoId());
                throw new DuplicateResourceException("Ya existe otro pago asociado a ese pedido");
            }
        });

        pago.setPedidoId(dto.pedidoId());
        pago.setMonto(dto.monto());
        pago.setEstado(dto.estado().trim().toUpperCase());

        Pago actualizado = pagoRepository.save(pago);

        logger.info("Pago actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarPago(Long id) {
        logger.info("Intentando eliminar pago id={}", id);

        if (!pagoRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Pago no encontrado con id={}", id);
            throw new ResourceNotFoundException("Pago no encontrado con id: " + id);
        }

        pagoRepository.deleteById(id);

        logger.info("Pago eliminado correctamente con id={}", id);
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            logger.warn("Monto inválido recibido={}", monto);
            throw new BadRequestException("El monto debe ser mayor a cero");
        }
    }

    private PagoResponseDTO mapToResponseDTO(Pago pago) {
        return new PagoResponseDTO(
                pago.getId(),
                pago.getPedidoId(),
                pago.getMonto(),
                pago.getEstado(),
                pago.getFecha()
        );
    }
}