package com.example.pago_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.pago_ms.dto.PagoRequestDTO;
import com.example.pago_ms.dto.PagoResponseDTO;
import com.example.pago_ms.service.PagoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagos")
@Validated
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crearPago(@Valid @RequestBody PagoRequestDTO dto) {
        PagoResponseDTO creado = pagoService.crearPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/procesar")
    public ResponseEntity<PagoResponseDTO> procesarPago(@Valid @RequestBody PagoRequestDTO dto) {
        PagoResponseDTO procesado = pagoService.procesarPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(procesado);
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarPagos() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(pedidoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> actualizarPago(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.ok(pagoService.actualizarPago(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}