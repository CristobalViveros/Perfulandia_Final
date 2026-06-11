package com.example.producto_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProductoMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductoMsApplication.class, args);
	}

}
