package com.example.clientes_ms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientes_ms.dto.ClienteRequestDTO;
import com.example.clientes_ms.dto.ClienteResponseDTO;
import com.example.clientes_ms.exception.DuplicateResourceException;
import com.example.clientes_ms.exception.ResourceNotFoundException;
import com.example.clientes_ms.model.Cliente;
import com.example.clientes_ms.repository.ClienteRepository;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        logger.info("Iniciando creación de cliente con email={}", dto.email());

        String emailNormalizado = dto.email().trim().toLowerCase();

        if (clienteRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            logger.warn("Intento de crear cliente duplicado con email={}", emailNormalizado);
            throw new DuplicateResourceException("Ya existe un cliente con ese email");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.nombre().trim());
        cliente.setApellidos(dto.apellidos().trim());
        cliente.setEmail(emailNormalizado);
        cliente.setTelefono(dto.telefono());
        cliente.setActivo(dto.activo() != null ? dto.activo() : true);

        Cliente guardado = clienteRepository.save(cliente);

        logger.info("Cliente creado correctamente con id={}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientes() {
        logger.info("Listando todos los clientes");

        return clienteRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorId(Long id) {
        logger.info("Buscando cliente por id={}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cliente no encontrado con id={}", id);
                    return new ResourceNotFoundException("Cliente no encontrado con id: " + id);
                });

        return mapToResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientesPorEstado(Boolean activo) {
        logger.info("Listando clientes por estado activo={}", activo);

        return clienteRepository.findByActivo(activo)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO dto) {
        logger.info("Iniciando actualización de cliente id={}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se puede actualizar. Cliente no encontrado con id={}", id);
                    return new ResourceNotFoundException("Cliente no encontrado con id: " + id);
                });

        String nuevoEmail = dto.email().trim().toLowerCase();

        clienteRepository.findByEmailIgnoreCase(nuevoEmail).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                logger.warn("Intento de actualizar cliente id={} con email ya usado={}", id, nuevoEmail);
                throw new DuplicateResourceException("Ya existe otro cliente con ese email");
            }
        });

        cliente.setNombre(dto.nombre().trim());
        cliente.setApellidos(dto.apellidos().trim());
        cliente.setEmail(nuevoEmail);
        cliente.setTelefono(dto.telefono());
        cliente.setActivo(dto.activo() != null ? dto.activo() : cliente.getActivo());

        Cliente actualizado = clienteRepository.save(cliente);

        logger.info("Cliente actualizado correctamente con id={}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    public void eliminarCliente(Long id) {
        logger.info("Intentando eliminar cliente id={}", id);

        if (!clienteRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Cliente no encontrado con id={}", id);
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }

        clienteRepository.deleteById(id);

        logger.info("Cliente eliminado correctamente con id={}", id);
    }

    private ClienteResponseDTO mapToResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getActivo(),
                cliente.getVersion()
        );
    }
}