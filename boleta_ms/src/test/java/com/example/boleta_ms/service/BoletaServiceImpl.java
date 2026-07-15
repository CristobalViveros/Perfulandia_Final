package com.example.boleta_ms.service;

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

import com.example.boleta_ms.client.ClienteClient;
import com.example.boleta_ms.client.PagoClient;
import com.example.boleta_ms.client.PedidoClient;
import com.example.boleta_ms.clientdto.ClienteClientDTO;
import com.example.boleta_ms.clientdto.PagoClientDTO;
import com.example.boleta_ms.clientdto.PedidoClientDTO;
import com.example.boleta_ms.dto.BoletaRequestDTO;
import com.example.boleta_ms.dto.BoletaResponseDTO;
import com.example.boleta_ms.exception.BadRequestException;
import com.example.boleta_ms.exception.DuplicateResourceException;
import com.example.boleta_ms.model.Boleta;
import com.example.boleta_ms.repository.BoletaRepository;

@ExtendWith(MockitoExtension.class)
class BoletaServiceImplTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private PagoClient pagoClient;

    @InjectMocks
    private BoletaServiceImpl boletaService;

    @Test
    void crearBoleta_deberiaCrearBoletaCorrectamente() {
        BoletaRequestDTO request = new BoletaRequestDTO(
                6L,
                5L,
                6L,
                new BigDecimal("45990.00"),
                "EMITIDA"
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

        PagoClientDTO pago = new PagoClientDTO(
                6L,
                6L,
                new BigDecimal("45990.00"),
                "APROBADO",
                LocalDateTime.now()
        );

        when(boletaRepository.existsByPagoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedido);
        when(pagoClient.obtenerPagoPorId(6L)).thenReturn(pago);

        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> {
            Boleta boleta = invocation.getArgument(0);
            boleta.setId(100L);
            return boleta;
        });

        BoletaResponseDTO response = boletaService.crearBoleta(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(6L, response.pedidoId());
        assertEquals(5L, response.clienteId());
        assertEquals(6L, response.pagoId());
        assertEquals(new BigDecimal("45990.00"), response.total());
        assertEquals("EMITIDA", response.estado());

        verify(boletaRepository).existsByPagoId(6L);
        verify(clienteClient).obtenerClientePorId(5L);
        verify(pedidoClient).obtenerPedidoPorId(6L);
        verify(pagoClient).obtenerPagoPorId(6L);
        verify(boletaRepository).save(any(Boleta.class));
    }

    @Test
    void crearBoleta_deberiaLanzarErrorSiPagoYaTieneBoleta() {
        BoletaRequestDTO request = new BoletaRequestDTO(
                6L,
                5L,
                6L,
                new BigDecimal("45990.00"),
                "EMITIDA"
        );

        when(boletaRepository.existsByPagoId(6L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> boletaService.crearBoleta(request)
        );

        assertEquals("Ya existe una boleta asociada a ese pago", exception.getMessage());

        verify(boletaRepository).existsByPagoId(6L);
        verifyNoInteractions(clienteClient);
        verifyNoInteractions(pedidoClient);
        verifyNoInteractions(pagoClient);
        verify(boletaRepository, never()).save(any(Boleta.class));
    }

    @Test
    void crearBoleta_deberiaLanzarErrorSiClienteEstaInactivo() {
        BoletaRequestDTO request = new BoletaRequestDTO(
                6L,
                5L,
                6L,
                new BigDecimal("45990.00"),
                "EMITIDA"
        );

        ClienteClientDTO clienteInactivo = new ClienteClientDTO(
                5L,
                "Cliente",
                "Inactivo",
                "cliente.inactivo@test.com",
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

        PagoClientDTO pago = new PagoClientDTO(
                6L,
                6L,
                new BigDecimal("45990.00"),
                "APROBADO",
                LocalDateTime.now()
        );

        when(boletaRepository.existsByPagoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteInactivo);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedido);
        when(pagoClient.obtenerPagoPorId(6L)).thenReturn(pago);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.crearBoleta(request)
        );

        assertEquals("El cliente asociado a la boleta está inactivo", exception.getMessage());

        verify(boletaRepository, never()).save(any(Boleta.class));
    }

    @Test
    void crearBoleta_deberiaLanzarErrorSiPedidoNoPerteneceAlCliente() {
        BoletaRequestDTO request = new BoletaRequestDTO(
                6L,
                5L,
                6L,
                new BigDecimal("45990.00"),
                "EMITIDA"
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

        PagoClientDTO pago = new PagoClientDTO(
                6L,
                6L,
                new BigDecimal("45990.00"),
                "APROBADO",
                LocalDateTime.now()
        );

        when(boletaRepository.existsByPagoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedidoDeOtroCliente);
        when(pagoClient.obtenerPagoPorId(6L)).thenReturn(pago);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.crearBoleta(request)
        );

        assertEquals("El pedido no pertenece al cliente indicado", exception.getMessage());

        verify(boletaRepository, never()).save(any(Boleta.class));
    }

    @Test
    void crearBoleta_deberiaLanzarErrorSiPagoNoEstaAprobado() {
        BoletaRequestDTO request = new BoletaRequestDTO(
                6L,
                5L,
                6L,
                new BigDecimal("45990.00"),
                "EMITIDA"
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

        PagoClientDTO pagoPendiente = new PagoClientDTO(
                6L,
                6L,
                new BigDecimal("45990.00"),
                "PENDIENTE",
                LocalDateTime.now()
        );

        when(boletaRepository.existsByPagoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedido);
        when(pagoClient.obtenerPagoPorId(6L)).thenReturn(pagoPendiente);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.crearBoleta(request)
        );

        assertEquals("No se puede emitir boleta porque el pago no está aprobado", exception.getMessage());

        verify(boletaRepository, never()).save(any(Boleta.class));
    }

    @Test
    void crearBoleta_deberiaLanzarErrorSiTotalNoCoincideConMontoPago() {
        BoletaRequestDTO request = new BoletaRequestDTO(
                6L,
                5L,
                6L,
                new BigDecimal("99999.00"),
                "EMITIDA"
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

        PagoClientDTO pago = new PagoClientDTO(
                6L,
                6L,
                new BigDecimal("45990.00"),
                "APROBADO",
                LocalDateTime.now()
        );

        when(boletaRepository.existsByPagoId(6L)).thenReturn(false);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(cliente);
        when(pedidoClient.obtenerPedidoPorId(6L)).thenReturn(pedido);
        when(pagoClient.obtenerPagoPorId(6L)).thenReturn(pago);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.crearBoleta(request)
        );

        assertEquals("El total de la boleta no coincide con el monto del pago", exception.getMessage());

        verify(boletaRepository, never()).save(any(Boleta.class));
    }
}