package com.example.inventario_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.inventario_ms.dto.InventarioRequestDTO;
import com.example.inventario_ms.dto.InventarioResponseDTO;
import com.example.inventario_ms.service.InventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventario")
@Validated
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crearInventario(@Valid @RequestBody InventarioRequestDTO dto) {
        InventarioResponseDTO creado = inventarioService.crearInventario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> listarInventario() {
        return ResponseEntity.ok(inventarioService.listarInventario());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> obtenerInventarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerInventarioPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponseDTO> obtenerInventarioPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerInventarioPorProducto(productoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<InventarioResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(inventarioService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> actualizarInventario(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.ok(inventarioService.actualizarInventario(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.noContent().build();
    }
}