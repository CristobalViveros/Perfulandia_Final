package com.example.boletas_ms.service;

import java.util.List;

import com.example.boletas_ms.dto.BoletaRequestDTO;
import com.example.boletas_ms.dto.BoletaResponseDTO;

public interface BoletaService {

    BoletaResponseDTO crearBoleta(BoletaRequestDTO dto);

    List<BoletaResponseDTO> listarBoletas();

    BoletaResponseDTO obtenerBoletaPorId(Long id);

    List<BoletaResponseDTO> obtenerBoletasPorCliente(Long clienteId);

    BoletaResponseDTO obtenerBoletaPorPedido(Long pedidoId);

    void eliminarBoleta(Long id);
}