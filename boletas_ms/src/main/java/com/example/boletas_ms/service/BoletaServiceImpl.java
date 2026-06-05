package com.example.boletas_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boletas_ms.dto.BoletaRequestDTO;
import com.example.boletas_ms.dto.BoletaResponseDTO;
import com.example.boletas_ms.exception.BadRequestException;
import com.example.boletas_ms.exception.DuplicateResourceException;
import com.example.boletas_ms.exception.ResourceNotFoundException;
import com.example.boletas_ms.model.Boleta;
import com.example.boletas_ms.repository.BoletaRepository;

@Service
@Transactional
public class BoletaServiceImpl implements BoletaService {

    private final BoletaRepository boletaRepository;

    public BoletaServiceImpl(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @Override
    public BoletaResponseDTO crearBoleta(BoletaRequestDTO dto) {

        if (dto.total().signum() <= 0) {
            throw new BadRequestException("El total debe ser mayor a cero");
        }

        if (boletaRepository.existsByPagoId(dto.pagoId())) {
            throw new DuplicateResourceException("Ya existe una boleta asociada a ese pago");
        }

        Boleta boleta = new Boleta();
        boleta.setPedidoId(dto.pedidoId());
        boleta.setClienteId(dto.clienteId());
        boleta.setPagoId(dto.pagoId());
        boleta.setTotal(dto.total());
        boleta.setEstado(dto.estado().trim().toUpperCase());
        boleta.setFechaEmision(LocalDateTime.now());

        Boleta guardada = boletaRepository.save(boleta);
        return mapToResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> listarBoletas() {
        return boletaRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BoletaResponseDTO obtenerBoletaPorId(Long id) {
        Boleta boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con id: " + id));

        return mapToResponseDTO(boleta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaResponseDTO> obtenerBoletasPorCliente(Long clienteId) {
        List<Boleta> boletas = boletaRepository.findByClienteId(clienteId);

        if (boletas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron boletas para el cliente con id: " + clienteId);
        }

        return boletas.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BoletaResponseDTO obtenerBoletaPorPedido(Long pedidoId) {
        Boleta boleta = boletaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró boleta para el pedido con id: " + pedidoId));

        return mapToResponseDTO(boleta);
    }

    @Override
    public void eliminarBoleta(Long id) {
        if (!boletaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Boleta no encontrada con id: " + id);
        }

        boletaRepository.deleteById(id);
    }

    private BoletaResponseDTO mapToResponseDTO(Boleta boleta) {
        return new BoletaResponseDTO(
                boleta.getId(),
                boleta.getPedidoId(),
                boleta.getClienteId(),
                boleta.getPagoId(),
                boleta.getTotal(),
                boleta.getEstado(),
                boleta.getFechaEmision()
        );
    }
}
