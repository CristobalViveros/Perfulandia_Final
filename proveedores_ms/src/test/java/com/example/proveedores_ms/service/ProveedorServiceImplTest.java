package com.example.proveedores_ms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.proveedores_ms.dto.ProveedorRequestDTO;
import com.example.proveedores_ms.dto.ProveedorResponseDTO;
import com.example.proveedores_ms.exception.DuplicateResourceException;
import com.example.proveedores_ms.exception.ResourceNotFoundException;
import com.example.proveedores_ms.model.Proveedor;
import com.example.proveedores_ms.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceImplTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorServiceImpl proveedorService;

    @Test
    void crearProveedor_deberiaCrearProveedorCorrectamente() {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Test",
                "76.123.456-7",
                "+56912345678",
                "proveedor@test.com",
                "Av. Test 123",
                "ACTIVO"
        );

        when(proveedorRepository.existsByRutIgnoreCase("76.123.456-7")).thenReturn(false);
        when(proveedorRepository.existsByEmailIgnoreCase("proveedor@test.com")).thenReturn(false);

        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> {
            Proveedor proveedor = invocation.getArgument(0);
            proveedor.setId(100L);
            proveedor.setVersion(0L);
            return proveedor;
        });

        ProveedorResponseDTO response = proveedorService.crearProveedor(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Proveedor Test", response.nombre());
        assertEquals("76.123.456-7", response.rut());
        assertEquals("+56912345678", response.telefono());
        assertEquals("proveedor@test.com", response.email());
        assertEquals("Av. Test 123", response.direccion());
        assertEquals("ACTIVO", response.estado());

        verify(proveedorRepository).existsByRutIgnoreCase("76.123.456-7");
        verify(proveedorRepository).existsByEmailIgnoreCase("proveedor@test.com");
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    void crearProveedor_deberiaLanzarErrorSiRutYaExiste() {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Test",
                "76.123.456-7",
                "+56912345678",
                "proveedor@test.com",
                "Av. Test 123",
                "ACTIVO"
        );

        when(proveedorRepository.existsByRutIgnoreCase("76.123.456-7")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> proveedorService.crearProveedor(request)
        );

        assertEquals("Ya existe un proveedor con ese RUT", exception.getMessage());

        verify(proveedorRepository).existsByRutIgnoreCase("76.123.456-7");
        verify(proveedorRepository, never()).existsByEmailIgnoreCase(any());
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void crearProveedor_deberiaLanzarErrorSiEmailYaExiste() {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Test",
                "76.123.456-7",
                "+56912345678",
                "proveedor@test.com",
                "Av. Test 123",
                "ACTIVO"
        );

        when(proveedorRepository.existsByRutIgnoreCase("76.123.456-7")).thenReturn(false);
        when(proveedorRepository.existsByEmailIgnoreCase("proveedor@test.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> proveedorService.crearProveedor(request)
        );

        assertEquals("Ya existe un proveedor con ese email", exception.getMessage());

        verify(proveedorRepository).existsByRutIgnoreCase("76.123.456-7");
        verify(proveedorRepository).existsByEmailIgnoreCase("proveedor@test.com");
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void obtenerProveedorPorId_deberiaRetornarProveedorCorrectamente() {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Uno");
        proveedor.setRut("76.123.456-7");
        proveedor.setTelefono("+56912345678");
        proveedor.setEmail("proveedor1@test.com");
        proveedor.setDireccion("Av. Uno 123");
        proveedor.setEstado("ACTIVO");
        proveedor.setVersion(0L);

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        ProveedorResponseDTO response = proveedorService.obtenerProveedorPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Proveedor Uno", response.nombre());
        assertEquals("76.123.456-7", response.rut());
        assertEquals("proveedor1@test.com", response.email());

        verify(proveedorRepository).findById(1L);
    }

    @Test
    void obtenerProveedorPorId_deberiaLanzarErrorSiNoExiste() {
        when(proveedorRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> proveedorService.obtenerProveedorPorId(999L)
        );

        assertEquals("Proveedor no encontrado con id: 999", exception.getMessage());

        verify(proveedorRepository).findById(999L);
    }

    @Test
    void listarProveedores_deberiaRetornarListaProveedores() {
        Proveedor proveedor1 = new Proveedor();
        proveedor1.setId(1L);
        proveedor1.setNombre("Proveedor Uno");
        proveedor1.setRut("76.111.111-1");
        proveedor1.setTelefono("+56911111111");
        proveedor1.setEmail("uno@test.com");
        proveedor1.setDireccion("Av. Uno");
        proveedor1.setEstado("ACTIVO");
        proveedor1.setVersion(0L);

        Proveedor proveedor2 = new Proveedor();
        proveedor2.setId(2L);
        proveedor2.setNombre("Proveedor Dos");
        proveedor2.setRut("76.222.222-2");
        proveedor2.setTelefono("+56922222222");
        proveedor2.setEmail("dos@test.com");
        proveedor2.setDireccion("Av. Dos");
        proveedor2.setEstado("ACTIVO");
        proveedor2.setVersion(0L);

        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor1, proveedor2));

        List<ProveedorResponseDTO> response = proveedorService.listarProveedores();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Proveedor Uno", response.get(0).nombre());
        assertEquals("Proveedor Dos", response.get(1).nombre());

        verify(proveedorRepository).findAll();
    }

    @Test
    void actualizarProveedor_deberiaActualizarProveedorCorrectamente() {
        Long id = 1L;

        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Editado",
                "76.999.999-9",
                "+56999999999",
                "editado@test.com",
                "Av. Editada 999",
                "ACTIVO"
        );

        Proveedor proveedorExistente = new Proveedor();
        proveedorExistente.setId(id);
        proveedorExistente.setNombre("Proveedor Antiguo");
        proveedorExistente.setRut("76.123.456-7");
        proveedorExistente.setTelefono("+56912345678");
        proveedorExistente.setEmail("antiguo@test.com");
        proveedorExistente.setDireccion("Av. Antigua");
        proveedorExistente.setEstado("ACTIVO");
        proveedorExistente.setVersion(0L);

        when(proveedorRepository.findById(id)).thenReturn(Optional.of(proveedorExistente));
        when(proveedorRepository.findByRutIgnoreCase("76.999.999-9")).thenReturn(Optional.empty());
        when(proveedorRepository.findByEmailIgnoreCase("editado@test.com")).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProveedorResponseDTO response = proveedorService.actualizarProveedor(id, request);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Proveedor Editado", response.nombre());
        assertEquals("76.999.999-9", response.rut());
        assertEquals("+56999999999", response.telefono());
        assertEquals("editado@test.com", response.email());
        assertEquals("Av. Editada 999", response.direccion());
        assertEquals("ACTIVO", response.estado());

        verify(proveedorRepository).findById(id);
        verify(proveedorRepository).findByRutIgnoreCase("76.999.999-9");
        verify(proveedorRepository).findByEmailIgnoreCase("editado@test.com");
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    void actualizarProveedor_deberiaLanzarErrorSiProveedorNoExiste() {
        Long id = 999L;

        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Editado",
                "76.999.999-9",
                "+56999999999",
                "editado@test.com",
                "Av. Editada 999",
                "ACTIVO"
        );

        when(proveedorRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> proveedorService.actualizarProveedor(id, request)
        );

        assertEquals("Proveedor no encontrado con id: 999", exception.getMessage());

        verify(proveedorRepository).findById(id);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void eliminarProveedor_deberiaEliminarProveedorCorrectamente() {
        Long id = 1L;

        when(proveedorRepository.existsById(id)).thenReturn(true);

        proveedorService.eliminarProveedor(id);

        verify(proveedorRepository).existsById(id);
        verify(proveedorRepository).deleteById(id);
    }

    @Test
    void eliminarProveedor_deberiaLanzarErrorSiProveedorNoExiste() {
        Long id = 999L;

        when(proveedorRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> proveedorService.eliminarProveedor(id)
        );

        assertEquals("Proveedor no encontrado con id: 999", exception.getMessage());

        verify(proveedorRepository).existsById(id);
        verify(proveedorRepository, never()).deleteById(id);
    }
}