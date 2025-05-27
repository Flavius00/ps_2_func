package com.example.demo.config;

import com.example.demo.service.MockDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializationConfig {

    /**
     * Bean pentru inițializarea datelor mock în mediul de dezvoltare
     * Se execută doar pentru profile-ul 'dev' sau când nu există alt profil activ
     */
    @Bean
    @Profile({"dev", "default"})
    public CommandLineRunner initializeData(MockDataService mockDataService) {
        return args -> {
            System.out.println("Initializing application data...");
            // MockDataService va fi apelat automat prin @PostConstruct
            System.out.println("Data initialization completed.");
        };
    }
}