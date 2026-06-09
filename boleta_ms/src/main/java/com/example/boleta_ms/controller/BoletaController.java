package com.example.boleta_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.boleta_ms.dto.BoletaRequestDTO;
import com.example.boleta_ms.dto.BoletaResponseDTO;
import com.example.boleta_ms.service.BoletaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/boletas")
@Validated
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    @PostMapping
    public ResponseEntity<BoletaResponseDTO> crearBoleta(@Valid @RequestBody BoletaRequestDTO dto) {
        BoletaResponseDTO creada = boletaService.crearBoleta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<BoletaResponseDTO>> listarBoletas() {
        return ResponseEntity.ok(boletaService.listarBoletas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> obtenerBoletaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.obtenerBoletaPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<BoletaResponseDTO>> obtenerBoletasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(boletaService.obtenerBoletasPorCliente(clienteId));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<BoletaResponseDTO> obtenerBoletaPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(boletaService.obtenerBoletaPorPedido(pedidoId));
    }

    @GetMapping("/pago/{pagoId}")
    public ResponseEntity<BoletaResponseDTO> obtenerBoletaPorPago(@PathVariable Long pagoId) {
        return ResponseEntity.ok(boletaService.obtenerBoletaPorPago(pagoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<BoletaResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(boletaService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> actualizarBoleta(
            @PathVariable Long id,
            @Valid @RequestBody BoletaRequestDTO dto) {
        return ResponseEntity.ok(boletaService.actualizarBoleta(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBoleta(@PathVariable Long id) {
        boletaService.eliminarBoleta(id);
        return ResponseEntity.noContent().build();
    }
}