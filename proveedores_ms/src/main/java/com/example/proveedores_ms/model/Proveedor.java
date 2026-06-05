package com.example.proveedores_ms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String rut;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false, length = 20)
    private String estado;

    @Version
    private Long version;
}
