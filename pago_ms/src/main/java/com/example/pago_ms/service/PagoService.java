package com.example.pago_ms.service;

import java.util.List;

import com.example.pago_ms.dto.PagoRequestDTO;
import com.example.pago_ms.dto.PagoResponseDTO;

public interface PagoService {

    PagoResponseDTO crearPago(PagoRequestDTO dto);

    PagoResponseDTO procesarPago(PagoRequestDTO dto);

    List<PagoResponseDTO> listarPagos();

    PagoResponseDTO obtenerPagoPorId(Long id);

    PagoResponseDTO obtenerPagoPorPedido(Long pedidoId);

    List<PagoResponseDTO> listarPorEstado(String estado);

    PagoResponseDTO actualizarPago(Long id, PagoRequestDTO dto);

    void eliminarPago(Long id);
}
