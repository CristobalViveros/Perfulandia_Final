package com.example.envio_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.envio_ms.dto.EnvioRequestDTO;
import com.example.envio_ms.dto.EnvioResponseDTO;
import com.example.envio_ms.service.EnvioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/envios")
@Validated
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(@Valid @RequestBody EnvioRequestDTO dto) {
        EnvioResponseDTO creado = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarEnvios() {
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerEnvioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioResponseDTO> obtenerEnvioPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorPedido(pedidoId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnvioResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(envioService.listarPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(envioService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @PathVariable Long id,
            @Valid @RequestBody EnvioRequestDTO dto) {
        return ResponseEntity.ok(envioService.actualizarEnvio(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long id) {
        envioService.eliminarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}
