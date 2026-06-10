package com.example.boleta_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.boleta_ms.clientdto.ClienteClientDTO;
import com.example.boleta_ms.config.FeignClientConfig;

@FeignClient(
        name = "clientes-ms",
        url = "${services.clientes.url}",
        configuration = FeignClientConfig.class
)
public interface ClienteClient {

    @GetMapping("/clientes/{id}")
    ClienteClientDTO obtenerClientePorId(@PathVariable("id") Long id);
}
