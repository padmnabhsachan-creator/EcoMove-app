package com.example.ecomove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.ecomove.controller",
    "com.example.ecomove.service",
    "com.example.ecomove.travel.qr"
})
public class EcomovebackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomovebackendApplication.class, args);
	}

}
