package com.example.clientes_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.clientes_ms.dto.ClienteRequestDTO;
import com.example.clientes_ms.dto.ClienteResponseDTO;
import com.example.clientes_ms.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
@Validated
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(@Valid @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO creado = clienteService.crearCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }

    @GetMapping("/estado/{activo}")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesPorEstado(@PathVariable Boolean activo) {
        return ResponseEntity.ok(clienteService.listarClientesPorEstado(activo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}

