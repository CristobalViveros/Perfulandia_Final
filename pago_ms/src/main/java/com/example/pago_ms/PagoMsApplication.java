package com.example.pago_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PagoMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagoMsApplication.class, args);
	}

}
