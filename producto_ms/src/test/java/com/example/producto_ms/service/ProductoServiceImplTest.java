package com.example.producto_ms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.producto_ms.client.CategoriaClient;
import com.example.producto_ms.client.ProveedorClient;
import com.example.producto_ms.clientdto.CategoriaClientDTO;
import com.example.producto_ms.clientdto.ProveedorClientDTO;
import com.example.producto_ms.dto.ProductoRequestDTO;
import com.example.producto_ms.dto.ProductoResponseDTO;
import com.example.producto_ms.exception.BadRequestException;
import com.example.producto_ms.exception.DuplicateResourceException;
import com.example.producto_ms.model.Producto;
import com.example.producto_ms.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaClient categoriaClient;

    @Mock
    private ProveedorClient proveedorClient;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void crearProducto_deberiaCrearProductoCorrectamente() {
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Producto Test Mockito",
                "Producto creado desde test unitario",
                new BigDecimal("15990.00"),
                "Perfulandia",
                "ACTIVO",
                1L,
                1L
        );

        CategoriaClientDTO categoria = new CategoriaClientDTO(
                1L,
                "Perfumes Mujer",
                "Fragancias femeninas",
                "ACTIVA"
        );

        ProveedorClientDTO proveedor = new ProveedorClientDTO(
                1L,
                "Proveedor Test",
                "76.123.456-7",
                "+56912345678",
                "proveedor@test.com",
                "Av. Test 123",
                "ACTIVO",
                0L
        );

        when(productoRepository.existsByNombreIgnoreCase("Producto Test Mockito")).thenReturn(false);
        when(categoriaClient.obtenerCategoriaPorId(1L)).thenReturn(categoria);
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(proveedor);

        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setId(100L);
            return producto;
        });

        ProductoResponseDTO response = productoService.crearProducto(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Producto Test Mockito", response.nombre());
        assertEquals("Producto creado desde test unitario", response.descripcion());
        assertEquals(new BigDecimal("15990.00"), response.precio());
        assertEquals("Perfulandia", response.marca());
        assertEquals("ACTIVO", response.estado());
        assertEquals(1L, response.categoriaId());
        assertEquals(1L, response.proveedorId());

        verify(productoRepository).existsByNombreIgnoreCase("Producto Test Mockito");
        verify(categoriaClient).obtenerCategoriaPorId(1L);
        verify(proveedorClient).obtenerProveedorPorId(1L);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crearProducto_deberiaLanzarErrorSiProductoYaExiste() {
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Producto Test Mockito",
                "Producto duplicado",
                new BigDecimal("15990.00"),
                "Perfulandia",
                "ACTIVO",
                1L,
                1L
        );

        when(productoRepository.existsByNombreIgnoreCase("Producto Test Mockito")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> productoService.crearProducto(request)
        );

        assertEquals("Ya existe un producto con ese nombre", exception.getMessage());

        verify(productoRepository).existsByNombreIgnoreCase("Producto Test Mockito");
        verifyNoInteractions(categoriaClient);
        verifyNoInteractions(proveedorClient);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProducto_deberiaLanzarErrorSiCategoriaEstaInactiva() {
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Producto Categoria Inactiva",
                "Producto con categoría inactiva",
                new BigDecimal("15990.00"),
                "Perfulandia",
                "ACTIVO",
                20L,
                1L
        );

        CategoriaClientDTO categoriaInactiva = new CategoriaClientDTO(
                20L,
                "Categoría Descontinuada",
                "Categoría antigua",
                "INACTIVA"
        );

        when(productoRepository.existsByNombreIgnoreCase("Producto Categoria Inactiva")).thenReturn(false);
        when(categoriaClient.obtenerCategoriaPorId(20L)).thenReturn(categoriaInactiva);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productoService.crearProducto(request)
        );

        assertEquals("La categoría asociada al producto no está activa", exception.getMessage());

        verify(productoRepository).existsByNombreIgnoreCase("Producto Categoria Inactiva");
        verify(categoriaClient).obtenerCategoriaPorId(20L);
        verifyNoInteractions(proveedorClient);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProducto_deberiaLanzarErrorSiProveedorEstaInactivo() {
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Producto Proveedor Inactivo",
                "Producto con proveedor inactivo",
                new BigDecimal("15990.00"),
                "Perfulandia",
                "ACTIVO",
                1L,
                5L
        );

        CategoriaClientDTO categoria = new CategoriaClientDTO(
                1L,
                "Perfumes Mujer",
                "Fragancias femeninas",
                "ACTIVA"
        );

        ProveedorClientDTO proveedorInactivo = new ProveedorClientDTO(
                5L,
                "Proveedor Inactivo",
                "80.567.890-1",
                "+56911111111",
                "inactivo@test.com",
                "Av. Inactivo 123",
                "INACTIVO",
                0L
        );

        when(productoRepository.existsByNombreIgnoreCase("Producto Proveedor Inactivo")).thenReturn(false);
        when(categoriaClient.obtenerCategoriaPorId(1L)).thenReturn(categoria);
        when(proveedorClient.obtenerProveedorPorId(5L)).thenReturn(proveedorInactivo);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productoService.crearProducto(request)
        );

        assertEquals("El proveedor asociado al producto no está activo", exception.getMessage());

        verify(productoRepository).existsByNombreIgnoreCase("Producto Proveedor Inactivo");
        verify(categoriaClient).obtenerCategoriaPorId(1L);
        verify(proveedorClient).obtenerProveedorPorId(5L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProducto_deberiaLanzarErrorSiPrecioEsInvalido() {
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Producto Precio Invalido",
                "Producto con precio inválido",
                new BigDecimal("0.00"),
                "Perfulandia",
                "ACTIVO",
                1L,
                1L
        );

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productoService.crearProducto(request)
        );

        assertEquals("El precio debe ser mayor a cero", exception.getMessage());

        verifyNoInteractions(productoRepository);
        verifyNoInteractions(categoriaClient);
        verifyNoInteractions(proveedorClient);
    }

    @Test
    void actualizarProducto_deberiaActualizarProductoCorrectamente() {
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Producto Actualizado",
                "Producto actualizado desde Mockito",
                new BigDecimal("19990.00"),
                "Perfulandia",
                "ACTIVO",
                1L,
                1L
        );

        Producto productoExistente = new Producto();
        productoExistente.setId(10L);
        productoExistente.setNombre("Producto Antiguo");
        productoExistente.setDescripcion("Producto antes de actualizar");
        productoExistente.setPrecio(new BigDecimal("15990.00"));
        productoExistente.setMarca("Perfulandia");
        productoExistente.setEstado("ACTIVO");
        productoExistente.setCategoriaId(1L);
        productoExistente.setProveedorId(1L);

        CategoriaClientDTO categoria = new CategoriaClientDTO(
                1L,
                "Perfumes Mujer",
                "Fragancias femeninas",
                "ACTIVA"
        );

        ProveedorClientDTO proveedor = new ProveedorClientDTO(
                1L,
                "Proveedor Test",
                "76.123.456-7",
                "+56912345678",
                "proveedor@test.com",
                "Av. Test 123",
                "ACTIVO",
                0L
        );

        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.findByNombreIgnoreCase("Producto Actualizado")).thenReturn(Optional.empty());
        when(categoriaClient.obtenerCategoriaPorId(1L)).thenReturn(categoria);
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(proveedor);

        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoResponseDTO response = productoService.actualizarProducto(10L, request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Producto Actualizado", response.nombre());
        assertEquals("Producto actualizado desde Mockito", response.descripcion());
        assertEquals(new BigDecimal("19990.00"), response.precio());
        assertEquals("ACTIVO", response.estado());

        verify(productoRepository).findById(10L);
        verify(productoRepository).findByNombreIgnoreCase("Producto Actualizado");
        verify(categoriaClient).obtenerCategoriaPorId(1L);
        verify(proveedorClient).obtenerProveedorPorId(1L);
        verify(productoRepository).save(any(Producto.class));
    }
}