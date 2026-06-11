package com.example.envio_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EnvioMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvioMsApplication.class, args);
	}

}
