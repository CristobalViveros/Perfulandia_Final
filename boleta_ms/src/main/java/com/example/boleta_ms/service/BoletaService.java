package com.example.boleta_ms.service;

import java.util.List;

import com.example.boleta_ms.dto.BoletaRequestDTO;
import com.example.boleta_ms.dto.BoletaResponseDTO;

public interface BoletaService {

    BoletaResponseDTO crearBoleta(BoletaRequestDTO dto);

    List<BoletaResponseDTO> listarBoletas();

    BoletaResponseDTO obtenerBoletaPorId(Long id);

    List<BoletaResponseDTO> obtenerBoletasPorCliente(Long clienteId);

    BoletaResponseDTO obtenerBoletaPorPedido(Long pedidoId);

    BoletaResponseDTO obtenerBoletaPorPago(Long pagoId);

    List<BoletaResponseDTO> listarPorEstado(String estado);

    BoletaResponseDTO actualizarBoleta(Long id, BoletaRequestDTO dto);

    void eliminarBoleta(Long id);
}