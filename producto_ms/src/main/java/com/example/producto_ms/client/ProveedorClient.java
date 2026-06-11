package com.example.producto_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.producto_ms.clientdto.ProveedorClientDTO;
import com.example.producto_ms.config.FeignClientConfig;

@FeignClient(
        name = "proveedores-ms",
        url = "${services.proveedores.url}",
        configuration = FeignClientConfig.class
)
public interface ProveedorClient {

    @GetMapping("/proveedores/{id}")
    ProveedorClientDTO obtenerProveedorPorId(@PathVariable("id") Long id);
}
