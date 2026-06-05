package com.example.clientes_ms.service;

import java.util.List;

import com.example.clientes_ms.dto.ClienteRequestDTO;
import com.example.clientes_ms.dto.ClienteResponseDTO;

public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO dto);

    List<ClienteResponseDTO> listarClientes();

    ClienteResponseDTO obtenerClientePorId(Long id);

    List<ClienteResponseDTO> listarClientesPorEstado(Boolean activo);

    ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO dto);

    void eliminarCliente(Long id);
}
