package com.example.pedido_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.pedido_ms.dto.PedidoRequestDTO;
import com.example.pedido_ms.dto.PedidoResponseDTO;
import com.example.pedido_ms.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
@Validated
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO creado = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.ok(pedidoService.actualizarPedido(id, dto));
    }

    @PutMapping("/confirmar/{id}")
    public ResponseEntity<PedidoResponseDTO> confirmarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}