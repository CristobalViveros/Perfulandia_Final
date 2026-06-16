package com.example.pedido_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.pedido_ms.clientdto.ClienteClientDTO;
import com.example.pedido_ms.config.FeignClientConfig;

@FeignClient(
        name = "clientes-ms",
        configuration = FeignClientConfig.class
)
public interface ClienteClient {

    @GetMapping("/clientes/{id}")
    ClienteClientDTO obtenerClientePorId(@PathVariable("id") Long id);
}