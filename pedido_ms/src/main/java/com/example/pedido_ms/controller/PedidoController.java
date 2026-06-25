package com.example.pedido_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.pedido_ms.dto.ErrorResponseDTO;
import com.example.pedido_ms.dto.PedidoRequestDTO;
import com.example.pedido_ms.dto.PedidoResponseDTO;
import com.example.pedido_ms.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
@Tag(name= "pedidos", description="Operacion relacionada con pedido")
@Validated
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    
    @Operation(summary = "Crear un pedido", description = "Crea un nuevo pedido validando cliente, productos e inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO creado = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    
    @Operation(summary = "Listar pedidos", description = "Obtiene todos los pedidos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    
    @Operation(summary = "Obtener pedido por ID", description = "Busca un pedido por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    @Operation(summary = "Listar pedidos por cliente", description = "Obtiene todos los pedidos de un cliente específico")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }


    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    
    @Operation(summary = "Actualizar pedido", description = "Actualiza un pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.ok(pedidoService.actualizarPedido(id, dto));
    }
    
    @Operation(summary = "Confirmar pedido", description = "Confirma un pedido y valida stock")
    @ApiResponse(responseCode = "200", description = "Pedido confirmado correctamente")
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<PedidoResponseDTO> confirmarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(id));
    }
    
    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}