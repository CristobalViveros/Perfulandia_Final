package com.example.inventario_ms.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventario")
@Getter
@Setter
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productoId;

    @Column(nullable = false)
    private Integer stockActual;

    @Column
    private Integer stockMinimo;

    @Column(length = 100)
    private String ubicacion;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
}