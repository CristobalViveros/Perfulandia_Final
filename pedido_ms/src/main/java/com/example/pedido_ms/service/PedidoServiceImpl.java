package com.example.pedido_ms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pedido_ms.dto.DetallePedidoResponseDTO;
import com.example.pedido_ms.dto.PedidoRequestDTO;
import com.example.pedido_ms.dto.PedidoResponseDTO;
import com.example.pedido_ms.exception.BadRequestException;
import com.example.pedido_ms.exception.ResourceNotFoundException;
import com.example.pedido_ms.model.DetallePedido;
import com.example.pedido_ms.model.Pedido;
import com.example.pedido_ms.repository.PedidoRepository;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO dto) {
        logger.info("Iniciando creación de pedido para clienteId={}", dto.clienteId());

        validarDetalles(dto);

        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.clienteId());
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(dto.estado() != null ? dto.estado().trim().toUpperCase() : "PENDIENTE");

        List<DetallePedido> detalles = dto.detalles().stream().map(detalleDTO -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(detalleDTO.productoId());
            detalle.setCantidad(detalleDTO.cantidad());
            detalle.setPrecioUnitario(detalleDTO.precioUnitario());
            detalle.setPedido(pedido);
            return detalle;
        }).toList();

        pedido.getDetalles().clear();
        pedido.getDetalles().addAll(detalles);

        Pedido guardado = pedidoRepository.save(pedido);

        logger.info("Pedido creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidos() {
        logger.info("Listando todos los pedidos");

        return pedidoRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        logger.info("Buscando pedido por id={}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con id={}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con id: " + id);
                });

        return mapToResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        logger.info("Listando pedidos por clienteId={}", clienteId);

        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando pedidos por estado={}", estado);

        return pedidoRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public PedidoResponseDTO actualizarPedido(Long id, PedidoRequestDTO dto) {
        logger.info("Iniciando actualización de pedido id={}", id);

        validarDetalles(dto);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Pedido no encontrado con id={}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con id: " + id);
                });

        pedido.setClienteId(dto.clienteId());
        pedido.setEstado(dto.estado() != null ? dto.estado().trim().toUpperCase() : pedido.getEstado());

        pedido.getDetalles().clear();

        List<DetallePedido> nuevosDetalles = dto.detalles().stream().map(detalleDTO -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(detalleDTO.productoId());
            detalle.setCantidad(detalleDTO.cantidad());
            detalle.setPrecioUnitario(detalleDTO.precioUnitario());
            detalle.setPedido(pedido);
            return detalle;
        }).toList();

        pedido.getDetalles().addAll(nuevosDetalles);

        Pedido actualizado = pedidoRepository.save(pedido);

        logger.info("Pedido actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public PedidoResponseDTO confirmarPedido(Long id) {
        logger.info("Confirmando pedido id={}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede confirmar. Pedido no encontrado con id={}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con id: " + id);
                });

        if ("CONFIRMADO".equalsIgnoreCase(pedido.getEstado())) {
            logger.warn("Intento de confirmar pedido ya confirmado id={}", id);
            throw new BadRequestException("El pedido ya se encuentra confirmado");
        }

        pedido.setEstado("CONFIRMADO");

        Pedido confirmado = pedidoRepository.save(pedido);

        logger.info("Pedido confirmado correctamente con id={}", confirmado.getId());

        return mapToResponseDTO(confirmado);
    }

    @Override
    public void eliminarPedido(Long id) {
        logger.info("Intentando eliminar pedido id={}", id);

        if (!pedidoRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Pedido no encontrado con id={}", id);
            throw new ResourceNotFoundException("Pedido no encontrado con id: " + id);
        }

        pedidoRepository.deleteById(id);

        logger.info("Pedido eliminado correctamente con id={}", id);
    }

    private void validarDetalles(PedidoRequestDTO dto) {
        if (dto.detalles() == null || dto.detalles().isEmpty()) {
            logger.warn("Pedido recibido sin detalles");
            throw new BadRequestException("El pedido debe tener al menos un detalle");
        }

        dto.detalles().forEach(detalle -> {
            if (detalle.cantidad() == null || detalle.cantidad() <= 0) {
                logger.warn("Cantidad inválida recibida={}", detalle.cantidad());
                throw new BadRequestException("La cantidad debe ser mayor a cero");
            }

            if (detalle.precioUnitario() == null || detalle.precioUnitario().signum() <= 0) {
                logger.warn("Precio unitario inválido recibido={}", detalle.precioUnitario());
                throw new BadRequestException("El precio unitario debe ser mayor a cero");
            }
        });
    }

    private PedidoResponseDTO mapToResponseDTO(Pedido pedido) {
        List<DetallePedidoResponseDTO> detalles = pedido.getDetalles().stream()
                .map(detalle -> new DetallePedidoResponseDTO(
                        detalle.getId(),
                        detalle.getProductoId(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                ))
                .toList();

        BigDecimal total = pedido.getDetalles().stream()
                .map(detalle -> detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getFecha(),
                pedido.getEstado(),
                total,
                detalles
        );
    }
}
