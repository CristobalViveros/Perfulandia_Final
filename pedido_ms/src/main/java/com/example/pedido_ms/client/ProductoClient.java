package com.example.pedido_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.pedido_ms.clientdto.ProductoClientDTO;
import com.example.pedido_ms.config.FeignClientConfig;

@FeignClient(
        name = "productos-ms",
        url = "${services.productos.url}",
        configuration = FeignClientConfig.class
)
public interface ProductoClient {

    @GetMapping("/productos/{id}")
    ProductoClientDTO obtenerProductoPorId(@PathVariable("id") Long id);
}