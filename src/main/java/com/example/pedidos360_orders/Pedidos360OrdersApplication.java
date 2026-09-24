package com.example.pedidos360_orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Pedidos360OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(Pedidos360OrdersApplication.class, args);
	}

}
