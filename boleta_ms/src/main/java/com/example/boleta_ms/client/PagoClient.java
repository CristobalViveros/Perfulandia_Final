package com.example.boleta_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.boleta_ms.clientdto.PagoClientDTO;
import com.example.boleta_ms.config.FeignClientConfig;

@FeignClient(
        name = "pago-ms",
        configuration = FeignClientConfig.class
)
public interface PagoClient {

    @GetMapping("/pagos/{id}")
    PagoClientDTO obtenerPagoPorId(@PathVariable("id") Long id);
}
