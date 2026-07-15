package com.example.pago_ms.service;

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

import com.example.pago_ms.client.PedidoClient;
import com.example.pago_ms.clientdto.PedidoClientDTO;
import com.example.pago_ms.dto.PagoRequestDTO;
import com.example.pago_ms.dto.PagoResponseDTO;
import com.example.pago_ms.exception.BadRequestException;
import com.example.pago_ms.exception.DuplicateResourceException;
import com.example.pago_ms.model.Pago;
import com.example.pago_ms.repository.PagoRepository;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    void crearPago_deberiaCrearPagoCorrectamente() {
        PagoRequestDTO request = new PagoRequestDTO(
                7L,
                new BigDecimal("45990.00"),
                "PENDIENTE"
        );

        PedidoClientDTO pedido = new PedidoClientDTO(
                7L,
                5L,
                LocalDateTime.now(),
                "CONFIRMADO",
                new BigDecimal("45990.00"),
                List.of()
        );

        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(pedido);
        when(pagoRepository.existsByPedidoId(7L)).thenReturn(false);

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setId(100L);
            pago.setFecha(LocalDateTime.now());
            return pago;
        });

        PagoResponseDTO response = pagoService.crearPago(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(7L, response.pedidoId());
        assertEquals(new BigDecimal("45990.00"), response.monto());
        assertEquals("PENDIENTE", response.estado());

        verify(pedidoClient).obtenerPedidoPorId(7L);
        verify(pagoRepository).existsByPedidoId(7L);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void crearPago_deberiaLanzarErrorSiPedidoYaTienePago() {
        PagoRequestDTO request = new PagoRequestDTO(
                7L,
                new BigDecimal("45990.00"),
                "PENDIENTE"
        );

        PedidoClientDTO pedido = new PedidoClientDTO(
                7L,
                5L,
                LocalDateTime.now(),
                "CONFIRMADO",
                new BigDecimal("45990.00"),
                List.of()
        );

        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(pedido);
        when(pagoRepository.existsByPedidoId(7L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> pagoService.crearPago(request)
        );

        assertEquals("Ya existe un pago asociado a ese pedido", exception.getMessage());

        verify(pedidoClient).obtenerPedidoPorId(7L);
        verify(pagoRepository).existsByPedidoId(7L);
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void crearPago_deberiaLanzarErrorSiPedidoEstaCancelado() {
        PagoRequestDTO request = new PagoRequestDTO(
                4L,
                new BigDecimal("59970.00"),
                "PENDIENTE"
        );

        PedidoClientDTO pedidoCancelado = new PedidoClientDTO(
                4L,
                1L,
                LocalDateTime.now(),
                "CANCELADO",
                new BigDecimal("59970.00"),
                List.of()
        );

        when(pedidoClient.obtenerPedidoPorId(4L)).thenReturn(pedidoCancelado);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pagoService.crearPago(request)
        );

        assertEquals("No se puede registrar pago para un pedido cancelado", exception.getMessage());

        verify(pedidoClient).obtenerPedidoPorId(4L);
        verify(pagoRepository, never()).existsByPedidoId(any());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void crearPago_deberiaLanzarErrorSiMontoNoCoincideConTotalPedido() {
        PagoRequestDTO request = new PagoRequestDTO(
                7L,
                new BigDecimal("99999.00"),
                "PENDIENTE"
        );

        PedidoClientDTO pedido = new PedidoClientDTO(
                7L,
                5L,
                LocalDateTime.now(),
                "CONFIRMADO",
                new BigDecimal("45990.00"),
                List.of()
        );

        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(pedido);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pagoService.crearPago(request)
        );

        assertEquals("El monto del pago no coincide con el total del pedido", exception.getMessage());

        verify(pedidoClient).obtenerPedidoPorId(7L);
        verify(pagoRepository, never()).existsByPedidoId(any());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void crearPago_deberiaLanzarErrorSiMontoEsInvalido() {
        PagoRequestDTO request = new PagoRequestDTO(
                7L,
                new BigDecimal("0.00"),
                "PENDIENTE"
        );

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pagoService.crearPago(request)
        );

        assertEquals("El monto debe ser mayor a cero", exception.getMessage());

        verifyNoInteractions(pedidoClient);
        verifyNoInteractions(pagoRepository);
    }
}