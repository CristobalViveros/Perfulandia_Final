package com.example.envio_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.envio_ms.clientdto.PedidoClientDTO;
import com.example.envio_ms.config.FeignClientConfig;

@FeignClient(
        name = "pedidos-ms",
        configuration = FeignClientConfig.class
)
public interface PedidoClient {

    @GetMapping("/pedidos/{id}")
    PedidoClientDTO obtenerPedidoPorId(@PathVariable("id") Long id);
}