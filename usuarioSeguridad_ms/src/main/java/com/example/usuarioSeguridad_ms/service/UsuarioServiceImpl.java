package com.example.usuarioSeguridad_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.usuarioSeguridad_ms.dto.UsuarioRequestDTO;
import com.example.usuarioSeguridad_ms.dto.UsuarioResponseDTO;
import com.example.usuarioSeguridad_ms.exception.DuplicateResourceException;
import com.example.usuarioSeguridad_ms.exception.ResourceNotFoundException;
import com.example.usuarioSeguridad_ms.model.Usuario;
import com.example.usuarioSeguridad_ms.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        logger.info("Iniciando creación de usuario username={}", dto.username());

        String usernameNormalizado = dto.username().trim().toLowerCase();
        String emailNormalizado = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByUsernameIgnoreCase(usernameNormalizado)) {
            logger.warn("Intento de crear usuario duplicado username={}", usernameNormalizado);
            throw new DuplicateResourceException("Ya existe un usuario con ese username");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            logger.warn("Intento de crear usuario con email duplicado={}", emailNormalizado);
            throw new DuplicateResourceException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usernameNormalizado);
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setEmail(emailNormalizado);
        usuario.setRol(dto.rol().trim().toUpperCase());
        usuario.setEstado(dto.estado().trim().toUpperCase());
        usuario.setFechaCreacion(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(usuario);

        logger.info("Usuario creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        logger.info("Listando todos los usuarios");

        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        logger.info("Buscando usuario por id={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con id={}", id);
                    return new ResourceNotFoundException("Usuario no encontrado con id: " + id);
                });

        return mapToResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorUsername(String username) {
        logger.info("Buscando usuario por username={}", username);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con username={}", username);
                    return new ResourceNotFoundException("Usuario no encontrado con username: " + username);
                });

        return mapToResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(String rol) {
        logger.info("Listando usuarios por rol={}", rol);

        return usuarioRepository.findByRolIgnoreCase(rol)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorEstado(String estado) {
        logger.info("Listando usuarios por estado={}", estado);

        return usuarioRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        logger.info("Iniciando actualización de usuario id={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Usuario no encontrado con id={}", id);
                    return new ResourceNotFoundException("Usuario no encontrado con id: " + id);
                });

        String nuevoUsername = dto.username().trim().toLowerCase();
        String nuevoEmail = dto.email().trim().toLowerCase();

        usuarioRepository.findByUsernameIgnoreCase(nuevoUsername).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar usuario id={} con username ya usado={}", id, nuevoUsername);
                throw new DuplicateResourceException("Ya existe otro usuario con ese username");
            }
        });

        usuarioRepository.findByEmailIgnoreCase(nuevoEmail).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar usuario id={} con email ya usado={}", id, nuevoEmail);
                throw new DuplicateResourceException("Ya existe otro usuario con ese email");
            }
        });

        usuario.setUsername(nuevoUsername);
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setEmail(nuevoEmail);
        usuario.setRol(dto.rol().trim().toUpperCase());
        usuario.setEstado(dto.estado().trim().toUpperCase());

        Usuario actualizado = usuarioRepository.save(usuario);

        logger.info("Usuario actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {
        logger.info("Intentando eliminar usuario id={}", id);

        if (!usuarioRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Usuario no encontrado con id={}", id);
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }

        usuarioRepository.deleteById(id);

        logger.info("Usuario eliminado correctamente con id={}", id);
    }

    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getFechaCreacion()
        );
    }
}