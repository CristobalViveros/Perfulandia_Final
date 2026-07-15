package com.example.clientes_ms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.clientes_ms.dto.ClienteRequestDTO;
import com.example.clientes_ms.dto.ClienteResponseDTO;
import com.example.clientes_ms.exception.DuplicateResourceException;
import com.example.clientes_ms.exception.ResourceNotFoundException;
import com.example.clientes_ms.model.Cliente;
import com.example.clientes_ms.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    void crearCliente_deberiaCrearClienteCorrectamente() {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Cliente Test",
                "Apellido Test",
                "cliente.test@correo.com",
                "123456789",
                true
        );

        when(clienteRepository.existsByEmailIgnoreCase("cliente.test@correo.com")).thenReturn(false);

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setId(100L);
            cliente.setVersion(0L);
            return cliente;
        });

        ClienteResponseDTO response = clienteService.crearCliente(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Cliente Test", response.nombre());
        assertEquals("Apellido Test", response.apellidos());
        assertEquals("cliente.test@correo.com", response.email());
        assertEquals("123456789", response.telefono());
        assertEquals(true, response.activo());

        verify(clienteRepository).existsByEmailIgnoreCase("cliente.test@correo.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void crearCliente_deberiaLanzarErrorSiEmailYaExiste() {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Cliente Test",
                "Apellido Test",
                "cliente.test@correo.com",
                "123456789",
                true
        );

        when(clienteRepository.existsByEmailIgnoreCase("cliente.test@correo.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> clienteService.crearCliente(request)
        );

        assertEquals("Ya existe un cliente con ese email", exception.getMessage());

        verify(clienteRepository).existsByEmailIgnoreCase("cliente.test@correo.com");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void obtenerClientePorId_deberiaRetornarClienteCorrectamente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Camila");
        cliente.setApellidos("Rojas");
        cliente.setEmail("camila@test.com");
        cliente.setTelefono("123456789");
        cliente.setActivo(true);
        cliente.setVersion(0L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO response = clienteService.obtenerClientePorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Camila", response.nombre());
        assertEquals("Rojas", response.apellidos());
        assertEquals("camila@test.com", response.email());

        verify(clienteRepository).findById(1L);
    }

    @Test
    void obtenerClientePorId_deberiaLanzarErrorSiClienteNoExiste() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.obtenerClientePorId(999L)
        );

        assertEquals("Cliente no encontrado con id: 999", exception.getMessage());

        verify(clienteRepository).findById(999L);
    }

    @Test
    void listarClientes_deberiaRetornarListaClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNombre("Camila");
        cliente1.setApellidos("Rojas");
        cliente1.setEmail("camila@test.com");
        cliente1.setTelefono("123456789");
        cliente1.setActivo(true);
        cliente1.setVersion(0L);

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("Matias");
        cliente2.setApellidos("Perez");
        cliente2.setEmail("matias@test.com");
        cliente2.setTelefono("987654321");
        cliente2.setActivo(true);
        cliente2.setVersion(0L);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));

        List<ClienteResponseDTO> response = clienteService.listarClientes();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Camila", response.get(0).nombre());
        assertEquals("Matias", response.get(1).nombre());

        verify(clienteRepository).findAll();
    }

    @Test
    void actualizarCliente_deberiaActualizarClienteCorrectamente() {
        Long id = 1L;

        ClienteRequestDTO request = new ClienteRequestDTO(
                "Cliente Editado",
                "Apellido Editado",
                "cliente.editado@correo.com",
                "999999999",
                true
        );

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(id);
        clienteExistente.setNombre("Cliente Antiguo");
        clienteExistente.setApellidos("Apellido Antiguo");
        clienteExistente.setEmail("cliente.antiguo@correo.com");
        clienteExistente.setTelefono("111111111");
        clienteExistente.setActivo(true);
        clienteExistente.setVersion(0L);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.findByEmailIgnoreCase("cliente.editado@correo.com")).thenReturn(Optional.empty());

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponseDTO response = clienteService.actualizarCliente(id, request);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Cliente Editado", response.nombre());
        assertEquals("Apellido Editado", response.apellidos());
        assertEquals("cliente.editado@correo.com", response.email());
        assertEquals("999999999", response.telefono());

        verify(clienteRepository).findById(id);
        verify(clienteRepository).findByEmailIgnoreCase("cliente.editado@correo.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_deberiaLanzarErrorSiClienteNoExiste() {
        Long id = 999L;

        ClienteRequestDTO request = new ClienteRequestDTO(
                "Cliente Editado",
                "Apellido Editado",
                "cliente.editado@correo.com",
                "999999999",
                true
        );

        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.actualizarCliente(id, request)
        );

        assertEquals("Cliente no encontrado con id: 999", exception.getMessage());

        verify(clienteRepository).findById(id);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void eliminarCliente_deberiaEliminarClienteCorrectamente() {
        Long id = 1L;

        when(clienteRepository.existsById(id)).thenReturn(true);

        clienteService.eliminarCliente(id);

        verify(clienteRepository).existsById(id);
        verify(clienteRepository).deleteById(id);
    }

    @Test
    void eliminarCliente_deberiaLanzarErrorSiClienteNoExiste() {
        Long id = 999L;

        when(clienteRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.eliminarCliente(id)
        );

        assertEquals("Cliente no encontrado con id: 999", exception.getMessage());

        verify(clienteRepository).existsById(id);
        verify(clienteRepository, never()).deleteById(id);
    }
}