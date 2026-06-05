package com.example.envio_ms.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "envios")
@Getter
@Setter
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false, length = 200)
    private String direccionEntrega;

    @Column(nullable = false, length = 80)
    private String comuna;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @Column(length = 150)
    private String ubicacionActual;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime ultimaActualizacion;

    @Version
    private Long version;
}