package com.example.boleta_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BoletaMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoletaMsApplication.class, args);
	}

}
