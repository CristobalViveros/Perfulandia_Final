package com.example.categoria_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.categoria_ms.dto.CategoriaRequestDTO;
import com.example.categoria_ms.dto.CategoriaResponseDTO;
import com.example.categoria_ms.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
@Validated
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO creada = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerCategoriaPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CategoriaResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(categoriaService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
