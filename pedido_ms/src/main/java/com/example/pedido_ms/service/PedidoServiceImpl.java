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

import com.example.pedido_ms.client.ClienteClient;
import com.example.pedido_ms.client.ProductoClient;
import com.example.pedido_ms.client.InventarioClient;

import com.example.pedido_ms.clientdto.ClienteClientDTO;
import com.example.pedido_ms.clientdto.ProductoClientDTO;
import com.example.pedido_ms.clientdto.InventarioClientDTO;

import feign.FeignException;


@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;


    
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ClienteClient clienteClient,
            ProductoClient productoClient,
            InventarioClient inventarioClient) {
        this.pedidoRepository = pedidoRepository;
        this.clienteClient = clienteClient;
        this.productoClient = productoClient;
        this.inventarioClient = inventarioClient;
    }

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO dto) {
        logger.info("Iniciando creación de pedido para clienteId={}", dto.clienteId());

        validarDetalles(dto);
        validarReferenciasExternas(dto);

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
        validarReferenciasExternas(dto);

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

    private void validarReferenciasExternas(PedidoRequestDTO dto) {
        try {
            ClienteClientDTO cliente = clienteClient.obtenerClientePorId(dto.clienteId());

            if (cliente.activo() != null && !cliente.activo()) {
                throw new BadRequestException("El cliente asociado al pedido está inactivo");
            }

            dto.detalles().forEach(detalle -> {
                ProductoClientDTO producto = productoClient.obtenerProductoPorId(detalle.productoId());

                if (!"ACTIVO".equalsIgnoreCase(producto.estado())) {
                    throw new BadRequestException("El producto con id " + detalle.productoId() + " no está activo");
                }

                if (producto.precio() != null && producto.precio().compareTo(detalle.precioUnitario()) != 0) {
                    throw new BadRequestException("El precio unitario del producto " + detalle.productoId()
                            + " no coincide con el precio registrado");
                }

                InventarioClientDTO inventario = inventarioClient.obtenerInventarioPorProducto(detalle.productoId());

                if (inventario.stockActual() == null || inventario.stockActual() < detalle.cantidad()) {
                    throw new BadRequestException("Stock insuficiente para el producto con id " + detalle.productoId());
                }

                if ("SIN_STOCK".equalsIgnoreCase(inventario.estado())) {
                    throw new BadRequestException("El producto con id " + detalle.productoId() + " está sin stock");
                }
            });

        } catch (FeignException.NotFound ex) {
            logger.warn("Referencia externa no encontrada al crear/actualizar pedido", ex);
            throw new ResourceNotFoundException("No se encontró cliente, producto o inventario asociado");
        } catch (FeignException.Unauthorized ex) {
            logger.warn("No autorizado al consultar otro microservicio", ex);
            throw new BadRequestException("No autorizado al consultar otro microservicio");
        } catch (FeignException ex) {
            logger.error("Error Feign al comunicarse con otro microservicio. Status={}", ex.status(), ex);
            throw new BadRequestException("Error al comunicarse con otro microservicio: " + ex.status());
        }
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
