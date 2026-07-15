package com.example.usuarioSeguridad_ms.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.usuarioSeguridad_ms.dto.UsuarioRequestDTO;
import com.example.usuarioSeguridad_ms.dto.UsuarioResponseDTO;
import com.example.usuarioSeguridad_ms.exception.DuplicateResourceException;
import com.example.usuarioSeguridad_ms.exception.ResourceNotFoundException;
import com.example.usuarioSeguridad_ms.model.Usuario;
import com.example.usuarioSeguridad_ms.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void crearUsuario_deberiaCrearUsuarioCorrectamente() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "usuario_test",
                "1234",
                "usuario@test.com",
                "ADMIN",
                "ACTIVO"
        );

        when(usuarioRepository.existsByUsernameIgnoreCase("usuario_test")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("usuario@test.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("password_encriptada");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(100L);
            usuario.setFechaCreacion(LocalDateTime.now());
            return usuario;
        });

        UsuarioResponseDTO response = usuarioService.crearUsuario(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("usuario_test", response.username());
        assertEquals("usuario@test.com", response.email());
        assertEquals("ADMIN", response.rol());
        assertEquals("ACTIVO", response.estado());

        verify(usuarioRepository).existsByUsernameIgnoreCase("usuario_test");
        verify(usuarioRepository).existsByEmailIgnoreCase("usuario@test.com");
        verify(passwordEncoder).encode("1234");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crearUsuario_deberiaLanzarErrorSiUsernameYaExiste() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "usuario_test",
                "1234",
                "usuario@test.com",
                "ADMIN",
                "ACTIVO"
        );

        when(usuarioRepository.existsByUsernameIgnoreCase("usuario_test")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> usuarioService.crearUsuario(request)
        );

        assertEquals("Ya existe un usuario con ese username", exception.getMessage());

        verify(usuarioRepository).existsByUsernameIgnoreCase("usuario_test");
        verify(usuarioRepository, never()).existsByEmailIgnoreCase(any());
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void crearUsuario_deberiaLanzarErrorSiEmailYaExiste() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "usuario_test",
                "1234",
                "usuario@test.com",
                "ADMIN",
                "ACTIVO"
        );

        when(usuarioRepository.existsByUsernameIgnoreCase("usuario_test")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("usuario@test.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> usuarioService.crearUsuario(request)
        );

        assertEquals("Ya existe un usuario con ese email", exception.getMessage());

        verify(usuarioRepository).existsByUsernameIgnoreCase("usuario_test");
        verify(usuarioRepository).existsByEmailIgnoreCase("usuario@test.com");
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void obtenerUsuarioPorId_deberiaRetornarUsuarioCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin_sistema");
        usuario.setPassword("password_encriptada");
        usuario.setEmail("admin@perfulandia.cl");
        usuario.setRol("ADMIN");
        usuario.setEstado("ACTIVO");
        usuario.setFechaCreacion(LocalDateTime.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("admin_sistema", response.username());
        assertEquals("admin@perfulandia.cl", response.email());
        assertEquals("ADMIN", response.rol());
        assertEquals("ACTIVO", response.estado());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerUsuarioPorId_deberiaLanzarErrorSiUsuarioNoExiste() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.obtenerUsuarioPorId(999L)
        );

        assertEquals("Usuario no encontrado con id: 999", exception.getMessage());

        verify(usuarioRepository).findById(999L);
    }

    @Test
    void listarUsuarios_deberiaRetornarListaUsuarios() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("admin_sistema");
        usuario1.setPassword("pass");
        usuario1.setEmail("admin@perfulandia.cl");
        usuario1.setRol("ADMIN");
        usuario1.setEstado("ACTIVO");
        usuario1.setFechaCreacion(LocalDateTime.now());

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("vendedor_01");
        usuario2.setPassword("pass");
        usuario2.setEmail("vendedor@perfulandia.cl");
        usuario2.setRol("VENDEDOR");
        usuario2.setEstado("ACTIVO");
        usuario2.setFechaCreacion(LocalDateTime.now());

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        List<UsuarioResponseDTO> response = usuarioService.listarUsuarios();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("admin_sistema", response.get(0).username());
        assertEquals("vendedor_01", response.get(1).username());

        verify(usuarioRepository).findAll();
    }

    @Test
    void actualizarUsuario_deberiaActualizarUsuarioCorrectamente() {
        Long id = 1L;

        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "usuario_editado",
                "5678",
                "usuario.editado@test.com",
                "ADMIN",
                "ACTIVO"
        );

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(id);
        usuarioExistente.setUsername("usuario_antiguo");
        usuarioExistente.setPassword("password_antigua");
        usuarioExistente.setEmail("usuario.antiguo@test.com");
        usuarioExistente.setRol("CLIENTE");
        usuarioExistente.setEstado("ACTIVO");
        usuarioExistente.setFechaCreacion(LocalDateTime.now());

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByUsernameIgnoreCase("usuario_editado")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmailIgnoreCase("usuario.editado@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("5678")).thenReturn("password_editada");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = usuarioService.actualizarUsuario(id, request);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("usuario_editado", response.username());
        assertEquals("usuario.editado@test.com", response.email());
        assertEquals("ADMIN", response.rol());
        assertEquals("ACTIVO", response.estado());

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).findByUsernameIgnoreCase("usuario_editado");
        verify(usuarioRepository).findByEmailIgnoreCase("usuario.editado@test.com");
        verify(passwordEncoder).encode("5678");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_deberiaLanzarErrorSiUsuarioNoExiste() {
        Long id = 999L;

        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "usuario_editado",
                "5678",
                "usuario.editado@test.com",
                "ADMIN",
                "ACTIVO"
        );

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.actualizarUsuario(id, request)
        );

        assertEquals("Usuario no encontrado con id: 999", exception.getMessage());

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void eliminarUsuario_deberiaEliminarUsuarioCorrectamente() {
        Long id = 1L;

        when(usuarioRepository.existsById(id)).thenReturn(true);

        usuarioService.eliminarUsuario(id);

        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository).deleteById(id);
    }

    @Test
    void eliminarUsuario_deberiaLanzarErrorSiUsuarioNoExiste() {
        Long id = 999L;

        when(usuarioRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.eliminarUsuario(id)
        );

        assertEquals("Usuario no encontrado con id: 999", exception.getMessage());

        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository, never()).deleteById(id);
    }
}