package com.example.pedido_ms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pedido_ms.client.ClienteClient;
import com.example.pedido_ms.client.InventarioClient;
import com.example.pedido_ms.client.ProductoClient;
import com.example.pedido_ms.clientdto.ClienteClientDTO;
import com.example.pedido_ms.clientdto.InventarioClientDTO;
import com.example.pedido_ms.clientdto.ProductoClientDTO;
import com.example.pedido_ms.dto.DetallePedidoRequestDTO;
import com.example.pedido_ms.dto.PedidoRequestDTO;
import com.example.pedido_ms.dto.PedidoResponseDTO;
import com.example.pedido_ms.exception.BadRequestException;
import com.example.pedido_ms.model.DetallePedido;
import com.example.pedido_ms.model.Pedido;
import com.example.pedido_ms.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    void crearPedido_deberiaCrearPedidoCorrectamente() {
        DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO(
                5L,
                1,
                new BigDecimal("45990.00")
        );

        PedidoRequestDTO request = new PedidoRequestDTO(
                5L,
                "PENDIENTE",
                List.of(detalle)
        );

        ClienteClientDTO cliente = new ClienteClientDTO(
                5L,
                "Fernanda",
                "Castillo",
                "fernanda@test.com",
                "123456789",
                true,
                0L
        );

        ProductoClientDTO producto = new ProductoClientDTO(
                5L,
                "Calvin Klein One",
                "Fragancia unisex fresca",
                new BigDecimal("45990.00"),
                "Calvin Klein",
                "ACTIVO",
                3L,
                1L
        );

        InventarioClientDTO inventario = new InventarioClientDTO(
                1L,
                5L,
                40,
                10,
                "Bodega Central",
                "DISPONIBLE",
                LocalDateTime.now(),
                0L
        );

        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(productoClient.obtenerProductoPorId(5L)).thenReturn(producto);
        when(inventarioClient.obtenerInventarioPorProducto(5L)).thenReturn(inventario);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(100L);
            pedido.setFecha(LocalDateTime.now());

            if (pedido.getDetalles() != null) {
                for (DetallePedido d : pedido.getDetalles()) {
                    d.setId(1L);
                    d.setPedido(pedido);
                }
            }

            return pedido;
        });

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(5L, response.clienteId());
        assertEquals("PENDIENTE", response.estado());

        verify(clienteClient).obtenerClientePorId(5L);
        verify(productoClient).obtenerProductoPorId(5L);
        verify(inventarioClient).obtenerInventarioPorProducto(5L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crearPedido_deberiaLanzarErrorSiClienteEstaInactivo() {
        DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO(
                5L,
                1,
                new BigDecimal("45990.00")
        );

        PedidoRequestDTO request = new PedidoRequestDTO(
                5L,
                "PENDIENTE",
                List.of(detalle)
        );

        ClienteClientDTO clienteInactivo = new ClienteClientDTO(
                5L,
                "Cliente",
                "Inactivo",
                "inactivo@test.com",
                "123456789",
                false,
                0L
        );

        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteInactivo);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crearPedido(request)
        );

        assertEquals("El cliente asociado al pedido está inactivo", exception.getMessage());

        verify(clienteClient).obtenerClientePorId(5L);
        verifyNoInteractions(productoClient);
        verifyNoInteractions(inventarioClient);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_deberiaLanzarErrorSiProductoNoEstaActivo() {
        DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO(
                5L,
                1,
                new BigDecimal("45990.00")
        );

        PedidoRequestDTO request = new PedidoRequestDTO(
                5L,
                "PENDIENTE",
                List.of(detalle)
        );

        ClienteClientDTO cliente = new ClienteClientDTO(
                5L,
                "Fernanda",
                "Castillo",
                "fernanda@test.com",
                "123456789",
                true,
                0L
        );

        ProductoClientDTO productoInactivo = new ProductoClientDTO(
                5L,
                "Producto Inactivo",
                "Producto no disponible",
                new BigDecimal("45990.00"),
                "Test",
                "INACTIVO",
                3L,
                1L
        );

        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(productoClient.obtenerProductoPorId(5L)).thenReturn(productoInactivo);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crearPedido(request)
        );

        assertEquals("El producto con id 5 no está activo", exception.getMessage());

        verify(clienteClient).obtenerClientePorId(5L);
        verify(productoClient).obtenerProductoPorId(5L);
        verifyNoInteractions(inventarioClient);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_deberiaLanzarErrorSiPrecioNoCoincide() {
        DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO(
                5L,
                1,
                new BigDecimal("99999.00")
        );

        PedidoRequestDTO request = new PedidoRequestDTO(
                5L,
                "PENDIENTE",
                List.of(detalle)
        );

        ClienteClientDTO cliente = new ClienteClientDTO(
                5L,
                "Fernanda",
                "Castillo",
                "fernanda@test.com",
                "123456789",
                true,
                0L
        );

        ProductoClientDTO producto = new ProductoClientDTO(
                5L,
                "Calvin Klein One",
                "Fragancia unisex fresca",
                new BigDecimal("45990.00"),
                "Calvin Klein",
                "ACTIVO",
                3L,
                1L
        );

        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(productoClient.obtenerProductoPorId(5L)).thenReturn(producto);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crearPedido(request)
        );

        assertEquals("El precio unitario del producto 5 no coincide con el precio registrado", exception.getMessage());

        verify(clienteClient).obtenerClientePorId(5L);
        verify(productoClient).obtenerProductoPorId(5L);
        verifyNoInteractions(inventarioClient);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_deberiaLanzarErrorSiNoHayStockSuficiente() {
        DetallePedidoRequestDTO detalle = new DetallePedidoRequestDTO(
                5L,
                999,
                new BigDecimal("45990.00")
        );

        PedidoRequestDTO request = new PedidoRequestDTO(
                5L,
                "PENDIENTE",
                List.of(detalle)
        );

        ClienteClientDTO cliente = new ClienteClientDTO(
                5L,
                "Fernanda",
                "Castillo",
                "fernanda@test.com",
                "123456789",
                true,
                0L
        );

        ProductoClientDTO producto = new ProductoClientDTO(
                5L,
                "Calvin Klein One",
                "Fragancia unisex fresca",
                new BigDecimal("45990.00"),
                "Calvin Klein",
                "ACTIVO",
                3L,
                1L
        );

        InventarioClientDTO inventario = new InventarioClientDTO(
                1L,
                5L,
                10,
                5,
                "Bodega Central",
                "DISPONIBLE",
                LocalDateTime.now(),
                0L
        );

        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(productoClient.obtenerProductoPorId(5L)).thenReturn(producto);
        when(inventarioClient.obtenerInventarioPorProducto(5L)).thenReturn(inventario);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crearPedido(request)
        );

        assertEquals("Stock insuficiente para el producto con id 5", exception.getMessage());

        verify(clienteClient).obtenerClientePorId(5L);
        verify(productoClient).obtenerProductoPorId(5L);
        verify(inventarioClient).obtenerInventarioPorProducto(5L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}