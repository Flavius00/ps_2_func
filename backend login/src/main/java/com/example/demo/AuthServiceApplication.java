package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ELIMINAT excluderile pentru JPA pentru a permite conectarea la MySQL
@SpringBootApplication
public class AuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);

		System.out.println("=".repeat(50));
		System.out.println("🔐 Authentication Service Started!");
		System.out.println("=".repeat(50));
		System.out.println("🌐 Service URL: http://localhost:8081");
		System.out.println("🔗 Login Endpoint: http://localhost:8081/auth/login");
		System.out.println("📝 Register Endpoint: http://localhost:8081/auth/register");
		System.out.println("📊 Database: MySQL (commercial_spaces_db)");
		System.out.println("=".repeat(50));
	}
}