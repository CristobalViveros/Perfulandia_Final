package com.example.pedido_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.pedido_ms.clientdto.InventarioClientDTO;
import com.example.pedido_ms.config.FeignClientConfig;

@FeignClient(
        name = "inventario-ms",
        url = "${services.inventario.url}",
        configuration = FeignClientConfig.class
)
public interface InventarioClient {

    @GetMapping("/inventario/producto/{productoId}")
    InventarioClientDTO obtenerInventarioPorProducto(@PathVariable("productoId") Long productoId);
}