package com.example.demo;

import com.example.demo.service.MockDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner run(MockDataService mockDataService) {
		return args -> {
			System.out.println("=".repeat(50));
			System.out.println("🚀 Commercial Spaces Application Started!");
			System.out.println("=".repeat(50));
			System.out.println("📊 Database: MySQL");
			System.out.println("🔗 Application URL: http://localhost:8080");
			System.out.println("🌐 Frontend URL: http://localhost:3000");
			System.out.println("=".repeat(50));

			// Verifică dacă datele mock au fost generate
			try {
				long spacesCount = mockDataService.getSpaces().size();
				if (spacesCount > 0) {
					System.out.println("✅ Mock data loaded successfully!");
					System.out.println("📈 Generated " + spacesCount + " commercial spaces");
				} else {
					System.out.println("ℹ️  No mock data found - will be generated on first run");
				}
			} catch (Exception e) {
				System.out.println("⚠️  Mock data service not ready yet - this is normal on first startup");
			}
			System.out.println("=".repeat(50));
		};
	}
}