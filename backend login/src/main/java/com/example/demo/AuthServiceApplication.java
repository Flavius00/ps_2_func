package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.example.demo.controller",
		"com.example.demo.service",
		"com.example.demo.mapper",
		"com.example.demo.config",
		"com.example.demo.exception"
})
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
		System.out.println("✅ DTOs and Validation: Enabled");
		System.out.println("🛡️ Exception Handling: Global");
		System.out.println("=".repeat(50));
	}
}