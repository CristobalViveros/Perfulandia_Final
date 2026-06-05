package com.example.envio_ms.service;

import java.util.List;

import com.example.envio_ms.dto.EnvioRequestDTO;
import com.example.envio_ms.dto.EnvioResponseDTO;

public interface EnvioService {

    EnvioResponseDTO crearEnvio(EnvioRequestDTO dto);

    List<EnvioResponseDTO> listarEnvios();

    EnvioResponseDTO obtenerEnvioPorId(Long id);

    EnvioResponseDTO obtenerEnvioPorPedido(Long pedidoId);

    List<EnvioResponseDTO> listarPorCliente(Long clienteId);

    List<EnvioResponseDTO> listarPorEstado(String estado);

    EnvioResponseDTO actualizarEnvio(Long id, EnvioRequestDTO dto);

    EnvioResponseDTO actualizarEstado(Long id, String estado);

    void eliminarEnvio(Long id);
}