package com.example.producto_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.producto_ms.clientdto.CategoriaClientDTO;
import com.example.producto_ms.config.FeignClientConfig;

@FeignClient(
        name = "categorias-ms",
        url = "${services.categorias.url}",
        configuration = FeignClientConfig.class
)
public interface CategoriaClient {

    @GetMapping("/categorias/{id}")
    CategoriaClientDTO obtenerCategoriaPorId(@PathVariable("id") Long id);
}