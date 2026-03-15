package com.kien.parking_system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ParkingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingSystemApplication.class, args);
	}
	@Bean
	CommandLineRunner started() {
		return args -> {
			System.out.println("🚀 SERVER PARKING SYSTEM STARTED SUCCESSFULLY!");
			System.out.println("🌐 http://localhost:8080");
		};
	}
}
