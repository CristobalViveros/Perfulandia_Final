package com.example.categoria_ms.service;

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

import com.example.categoria_ms.dto.CategoriaRequestDTO;
import com.example.categoria_ms.dto.CategoriaResponseDTO;
import com.example.categoria_ms.exception.DuplicateResourceException;
import com.example.categoria_ms.exception.ResourceNotFoundException;
import com.example.categoria_ms.model.Categoria;
import com.example.categoria_ms.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void crearCategoria_deberiaCrearCategoriaCorrectamente() {
        CategoriaRequestDTO request = new CategoriaRequestDTO(
                "Perfumes Test",
                "Categoría creada desde Mockito",
                "ACTIVA"
        );

        when(categoriaRepository.existsByNombreIgnoreCase("Perfumes Test")).thenReturn(false);

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria categoria = invocation.getArgument(0);
            categoria.setId(100L);
            return categoria;
        });

        CategoriaResponseDTO response = categoriaService.crearCategoria(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Perfumes Test", response.nombre());
        assertEquals("Categoría creada desde Mockito", response.descripcion());
        assertEquals("ACTIVA", response.estado());

        verify(categoriaRepository).existsByNombreIgnoreCase("Perfumes Test");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_deberiaLanzarErrorSiNombreYaExiste() {
        CategoriaRequestDTO request = new CategoriaRequestDTO(
                "Perfumes Test",
                "Categoría duplicada",
                "ACTIVA"
        );

        when(categoriaRepository.existsByNombreIgnoreCase("Perfumes Test")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoriaService.crearCategoria(request)
        );

        assertEquals("Ya existe una categoría con ese nombre", exception.getMessage());

        verify(categoriaRepository).existsByNombreIgnoreCase("Perfumes Test");
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void obtenerCategoriaPorId_deberiaRetornarCategoriaCorrectamente() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Perfumes Mujer");
        categoria.setDescripcion("Fragancias femeninas");
        categoria.setEstado("ACTIVA");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO response = categoriaService.obtenerCategoriaPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Perfumes Mujer", response.nombre());
        assertEquals("Fragancias femeninas", response.descripcion());
        assertEquals("ACTIVA", response.estado());

        verify(categoriaRepository).findById(1L);
    }

    @Test
    void obtenerCategoriaPorId_deberiaLanzarErrorSiNoExiste() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.obtenerCategoriaPorId(999L)
        );

        assertEquals("Categoria no encontrada con id: 999", exception.getMessage());

        verify(categoriaRepository).findById(999L);
    }

    @Test
    void listarCategorias_deberiaRetornarListaCategorias() {
        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNombre("Perfumes Mujer");
        categoria1.setDescripcion("Fragancias femeninas");
        categoria1.setEstado("ACTIVA");

        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNombre("Perfumes Hombre");
        categoria2.setDescripcion("Fragancias masculinas");
        categoria2.setEstado("ACTIVA");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria1, categoria2));

        List<CategoriaResponseDTO> response = categoriaService.listarCategorias();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Perfumes Mujer", response.get(0).nombre());
        assertEquals("Perfumes Hombre", response.get(1).nombre());

        verify(categoriaRepository).findAll();
    }

    @Test
    void actualizarCategoria_deberiaActualizarCategoriaCorrectamente() {
        Long id = 1L;

        CategoriaRequestDTO request = new CategoriaRequestDTO(
                "Perfumes Actualizados",
                "Descripción actualizada",
                "ACTIVA"
        );

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Perfumes Antiguos");
        categoriaExistente.setDescripcion("Descripción antigua");
        categoriaExistente.setEstado("ACTIVA");

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.findByNombreIgnoreCase("Perfumes Actualizados")).thenReturn(Optional.empty());

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaResponseDTO response = categoriaService.actualizarCategoria(id, request);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Perfumes Actualizados", response.nombre());
        assertEquals("Descripción actualizada", response.descripcion());
        assertEquals("ACTIVA", response.estado());

        verify(categoriaRepository).findById(id);
        verify(categoriaRepository).findByNombreIgnoreCase("Perfumes Actualizados");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_deberiaLanzarErrorSiCategoriaNoExiste() {
        Long id = 999L;

        CategoriaRequestDTO request = new CategoriaRequestDTO(
                "Perfumes Actualizados",
                "Descripción actualizada",
                "ACTIVA"
        );

        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.actualizarCategoria(id, request)
        );

        assertEquals("Categoria no encontrada con id: 999", exception.getMessage());

        verify(categoriaRepository).findById(id);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void eliminarCategoria_deberiaEliminarCategoriaCorrectamente() {
        Long id = 1L;

        when(categoriaRepository.existsById(id)).thenReturn(true);

        categoriaService.eliminarCategoria(id);

        verify(categoriaRepository).existsById(id);
        verify(categoriaRepository).deleteById(id);
    }

    @Test
    void eliminarCategoria_deberiaLanzarErrorSiCategoriaNoExiste() {
        Long id = 999L;

        when(categoriaRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.eliminarCategoria(id)
        );

        assertEquals("Categoria no encontrada con id: 999", exception.getMessage());

        verify(categoriaRepository).existsById(id);
        verify(categoriaRepository, never()).deleteById(id);
    }
}