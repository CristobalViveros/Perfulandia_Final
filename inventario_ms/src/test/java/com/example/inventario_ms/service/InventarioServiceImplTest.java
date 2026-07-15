package com.example.inventario_ms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.inventario_ms.dto.InventarioRequestDTO;
import com.example.inventario_ms.dto.InventarioResponseDTO;
import com.example.inventario_ms.exception.DuplicateResourceException;
import com.example.inventario_ms.exception.ResourceNotFoundException;
import com.example.inventario_ms.exception.BadRequestException;
import com.example.inventario_ms.model.Inventario;
import com.example.inventario_ms.repository.InventarioRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void crearInventario_deberiaCrearInventarioCorrectamente() {
        InventarioRequestDTO request = new InventarioRequestDTO(
                5L,
                40,
                10,
                "Bodega Central - Estante C1",
                "DISPONIBLE"
        );

        when(inventarioRepository.existsByProductoId(5L)).thenReturn(false);

        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> {
            Inventario inventario = invocation.getArgument(0);
            inventario.setId(100L);
            inventario.setFechaActualizacion(LocalDateTime.now());
            return inventario;
        });

        InventarioResponseDTO response = inventarioService.crearInventario(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(5L, response.productoId());
        assertEquals(40, response.stockActual());
        assertEquals(10, response.stockMinimo());
        assertEquals("Bodega Central - Estante C1", response.ubicacion());
        assertEquals("DISPONIBLE", response.estado());

        verify(inventarioRepository).existsByProductoId(5L);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void crearInventario_deberiaLanzarErrorSiProductoYaTieneInventario() {
        InventarioRequestDTO request = new InventarioRequestDTO(
                5L,
                40,
                10,
                "Bodega Central - Estante C1",
                "DISPONIBLE"
        );

        when(inventarioRepository.existsByProductoId(5L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> inventarioService.crearInventario(request)
        );

        assertEquals("Ya existe inventario para ese producto", exception.getMessage());

        verify(inventarioRepository).existsByProductoId(5L);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void crearInventario_deberiaLanzarErrorSiStockActualEsNegativo() {
        InventarioRequestDTO request = new InventarioRequestDTO(
                5L,
                -1,
                10,
                "Bodega Central - Estante C1",
                "DISPONIBLE"
        );

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> inventarioService.crearInventario(request)
        );

        assertEquals("El stock actual no puede ser negativo", exception.getMessage());

        verifyNoInteractions(inventarioRepository);
    }

    @Test
    void obtenerInventarioPorId_deberiaRetornarInventarioCorrectamente() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(5L);
        inventario.setStockActual(40);
        inventario.setStockMinimo(10);
        inventario.setUbicacion("Bodega Central - Estante C1");
        inventario.setEstado("DISPONIBLE");
        inventario.setFechaActualizacion(LocalDateTime.now());

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        InventarioResponseDTO response = inventarioService.obtenerInventarioPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(5L, response.productoId());
        assertEquals(40, response.stockActual());
        assertEquals("DISPONIBLE", response.estado());

        verify(inventarioRepository).findById(1L);
    }

    @Test
    void obtenerInventarioPorId_deberiaLanzarErrorSiNoExiste() {
        when(inventarioRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventarioService.obtenerInventarioPorId(999L)
        );

        assertEquals("Inventario no encontrado con id: 999", exception.getMessage());

        verify(inventarioRepository).findById(999L);
    }

    @Test
    void obtenerInventarioPorProducto_deberiaRetornarInventarioCorrectamente() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(5L);
        inventario.setStockActual(40);
        inventario.setStockMinimo(10);
        inventario.setUbicacion("Bodega Central - Estante C1");
        inventario.setEstado("DISPONIBLE");
        inventario.setFechaActualizacion(LocalDateTime.now());

        when(inventarioRepository.findByProductoId(5L)).thenReturn(Optional.of(inventario));

        InventarioResponseDTO response = inventarioService.obtenerInventarioPorProducto(5L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(5L, response.productoId());
        assertEquals(40, response.stockActual());

        verify(inventarioRepository).findByProductoId(5L);
    }

    @Test
    void listarInventario_deberiaRetornarListaInventario() {
        Inventario inventario1 = new Inventario();
        inventario1.setId(1L);
        inventario1.setProductoId(5L);
        inventario1.setStockActual(40);
        inventario1.setStockMinimo(10);
        inventario1.setUbicacion("Bodega Central - C1");
        inventario1.setEstado("DISPONIBLE");
        inventario1.setFechaActualizacion(LocalDateTime.now());

        Inventario inventario2 = new Inventario();
        inventario2.setId(2L);
        inventario2.setProductoId(6L);
        inventario2.setStockActual(0);
        inventario2.setStockMinimo(5);
        inventario2.setUbicacion("Bodega Central - C2");
        inventario2.setEstado("SIN_STOCK");
        inventario2.setFechaActualizacion(LocalDateTime.now());

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario1, inventario2));

        List<InventarioResponseDTO> response = inventarioService.listarInventario();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(5L, response.get(0).productoId());
        assertEquals(6L, response.get(1).productoId());

        verify(inventarioRepository).findAll();
    }

    @Test
    void actualizarInventario_deberiaActualizarInventarioCorrectamente() {
        Long id = 1L;

        InventarioRequestDTO request = new InventarioRequestDTO(
                5L,
                30,
                10,
                "Bodega Actualizada",
                "DISPONIBLE"
        );

        Inventario inventarioExistente = new Inventario();
        inventarioExistente.setId(id);
        inventarioExistente.setProductoId(5L);
        inventarioExistente.setStockActual(40);
        inventarioExistente.setStockMinimo(10);
        inventarioExistente.setUbicacion("Bodega Antigua");
        inventarioExistente.setEstado("DISPONIBLE");
        inventarioExistente.setFechaActualizacion(LocalDateTime.now());

        when(inventarioRepository.findById(id)).thenReturn(Optional.of(inventarioExistente));
        when(inventarioRepository.findByProductoId(5L)).thenReturn(Optional.of(inventarioExistente));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventarioResponseDTO response = inventarioService.actualizarInventario(id, request);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(5L, response.productoId());
        assertEquals(30, response.stockActual());
        assertEquals("Bodega Actualizada", response.ubicacion());
        assertEquals("DISPONIBLE", response.estado());

        verify(inventarioRepository).findById(id);
        verify(inventarioRepository).findByProductoId(5L);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void actualizarInventario_deberiaLanzarErrorSiNoExiste() {
        Long id = 999L;

        InventarioRequestDTO request = new InventarioRequestDTO(
                5L,
                30,
                10,
                "Bodega Actualizada",
                "DISPONIBLE"
        );

        when(inventarioRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventarioService.actualizarInventario(id, request)
        );

        assertEquals("Inventario no encontrado con id: 999", exception.getMessage());

        verify(inventarioRepository).findById(id);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void eliminarInventario_deberiaEliminarInventarioCorrectamente() {
        Long id = 1L;

        when(inventarioRepository.existsById(id)).thenReturn(true);

        inventarioService.eliminarInventario(id);

        verify(inventarioRepository).existsById(id);
        verify(inventarioRepository).deleteById(id);
    }

    @Test
    void eliminarInventario_deberiaLanzarErrorSiNoExiste() {
        Long id = 999L;

        when(inventarioRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventarioService.eliminarInventario(id)
        );

        assertEquals("Inventario no encontrado con id: 999", exception.getMessage());

        verify(inventarioRepository).existsById(id);
        verify(inventarioRepository, never()).deleteById(id);
    }
}