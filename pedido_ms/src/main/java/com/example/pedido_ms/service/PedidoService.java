package com.example.pedido_ms.service;

import java.util.List;

import com.example.pedido_ms.dto.PedidoRequestDTO;
import com.example.pedido_ms.dto.PedidoResponseDTO;

public interface PedidoService {

    PedidoResponseDTO crearPedido(PedidoRequestDTO dto);

    List<PedidoResponseDTO> listarPedidos();

    PedidoResponseDTO obtenerPedidoPorId(Long id);

    List<PedidoResponseDTO> listarPorCliente(Long clienteId);

    List<PedidoResponseDTO> listarPorEstado(String estado);

    PedidoResponseDTO actualizarPedido(Long id, PedidoRequestDTO dto);

    PedidoResponseDTO confirmarPedido(Long id);

    void eliminarPedido(Long id);
}