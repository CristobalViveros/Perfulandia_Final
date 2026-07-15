package com.example.envio_ms.service;

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

import com.example.envio_ms.client.ClienteClient;
import com.example.envio_ms.client.PedidoClient;
import com.example.envio_ms.clientdto.ClienteClientDTO;
import com.example.envio_ms.clientdto.PedidoClientDTO;
import com.example.envio_ms.dto.EnvioRequestDTO;
import com.example.envio_ms.dto.EnvioResponseDTO;
import com.example.envio_ms.exception.BadRequestException;
import com.example.envio_ms.exception.DuplicateResourceException;
import com.example.envio_ms.model.Envio;
import com.example.envio_ms.repository.EnvioRepository;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private EnvioServiceImpl envioService;

    @Test
    void crearEnvio_deberiaCrearEnvioCorrectamente() {
        EnvioRequestDTO request = new EnvioRequestDTO(
                6L,
                5L,
                "Av. Siempre Viva 742",
                "Puente Alto",
                "Santiago",
                "Bodega Central",
                "PENDIENTE"
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

        PedidoClientDTO pedido = new PedidoClientDTO(
                6L,
                5L,
                LocalDateTime.now(),
                "CONFIRMADO",
                new BigDecimal("45990.00"),
                List.of()
        );

        when(envioRepository.existsByPedidoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedido);

        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio envio = invocation.getArgument(0);
            envio.setId(100L);
            envio.setFechaCreacion(LocalDateTime.now());
            envio.setUltimaActualizacion(LocalDateTime.now());
            return envio;
        });

        EnvioResponseDTO response = envioService.crearEnvio(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(6L, response.pedidoId());
        assertEquals(5L, response.clienteId());
        assertEquals("Av. Siempre Viva 742", response.direccionEntrega());
        assertEquals("Puente Alto", response.comuna());
        assertEquals("Santiago", response.ciudad());
        assertEquals("Bodega Central", response.ubicacionActual());
        assertEquals("PENDIENTE", response.estado());

        verify(envioRepository).existsByPedidoId(6L);
        verify(clienteClient).obtenerClientePorId(5L);
        verify(pedidoClient).obtenerPedidoPorId(6L);
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiYaExisteEnvioParaPedido() {
        EnvioRequestDTO request = new EnvioRequestDTO(
                6L,
                5L,
                "Av. Siempre Viva 742",
                "Puente Alto",
                "Santiago",
                "Bodega Central",
                "PENDIENTE"
        );

        when(envioRepository.existsByPedidoId(6L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("Ya existe un envío asociado a ese pedido", exception.getMessage());

        verify(envioRepository).existsByPedidoId(6L);
        verifyNoInteractions(clienteClient);
        verifyNoInteractions(pedidoClient);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiClienteEstaInactivo() {
        EnvioRequestDTO request = new EnvioRequestDTO(
                6L,
                5L,
                "Av. Siempre Viva 742",
                "Puente Alto",
                "Santiago",
                "Bodega Central",
                "PENDIENTE"
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

        PedidoClientDTO pedido = new PedidoClientDTO(
                6L,
                5L,
                LocalDateTime.now(),
                "CONFIRMADO",
                new BigDecimal("45990.00"),
                List.of()
        );

        when(envioRepository.existsByPedidoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteInactivo);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedido);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("El cliente asociado al envío está inactivo", exception.getMessage());

        verify(envioRepository).existsByPedidoId(6L);
        verify(clienteClient).obtenerClientePorId(5L);
        verify(pedidoClient).obtenerPedidoPorId(6L);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiPedidoNoPerteneceAlCliente() {
        EnvioRequestDTO request = new EnvioRequestDTO(
                6L,
                5L,
                "Av. Siempre Viva 742",
                "Puente Alto",
                "Santiago",
                "Bodega Central",
                "PENDIENTE"
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

        PedidoClientDTO pedidoDeOtroCliente = new PedidoClientDTO(
                6L,
                99L,
                LocalDateTime.now(),
                "CONFIRMADO",
                new BigDecimal("45990.00"),
                List.of()
        );

        when(envioRepository.existsByPedidoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedidoDeOtroCliente);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("El pedido no pertenece al cliente indicado", exception.getMessage());

        verify(envioRepository).existsByPedidoId(6L);
        verify(clienteClient).obtenerClientePorId(5L);
        verify(pedidoClient).obtenerPedidoPorId(6L);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiPedidoNoEstaConfirmado() {
        EnvioRequestDTO request = new EnvioRequestDTO(
                3L,
                3L,
                "Av. Error 123",
                "Puente Alto",
                "Santiago",
                "Bodega Central",
                "PENDIENTE"
        );

        ClienteClientDTO cliente = new ClienteClientDTO(
                3L,
                "Valentina",
                "Morales",
                "valentina@test.com",
                "123456789",
                true,
                0L
        );

        PedidoClientDTO pedidoPendiente = new PedidoClientDTO(
                3L,
                3L,
                LocalDateTime.now(),
                "PENDIENTE",
                new BigDecimal("99990.00"),
                List.of()
        );

        when(envioRepository.existsByPedidoId(3L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(3L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(pedidoPendiente);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("No se puede crear envío porque el pedido no está confirmado", exception.getMessage());

        verify(envioRepository).existsByPedidoId(3L);
        verify(clienteClient).obtenerClientePorId(3L);
        verify(pedidoClient).obtenerPedidoPorId(3L);
        verify(envioRepository, never()).save(any(Envio.class));
    }
}