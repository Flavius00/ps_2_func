package com.example.demo.service.impl;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MockDataService;
import com.example.demo.service.MockDataServiceLogIn;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AuthMockDataServiceImpl implements MockDataServiceLogIn {
    private final UserRepository userRepository;

    public AuthMockDataServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @PostConstruct
    public void generateMockData() {
        // Create an owner user
        User owner = new User();
        owner.setId(1L);
        owner.setName("Adrian Popescu");
        owner.setEmail("adrian.popescu@example.com");
        owner.setUsername("adrianp");
        owner.setPassword("owner123");
        owner.setPhone("0745123456");
        owner.setAddress("Strada Alexandru Vlahuță 3, Cluj-Napoca");
        owner.setProfilePictureUrl("/assets/profile-adrian.jpg");
        owner.setRole(User.UserRole.OWNER);

        // Create a tenant user
        User tenant = new User();
        tenant.setId(2L);
        tenant.setName("Elena Dumitrescu");
        tenant.setEmail("elena.dumitrescu@example.com");
        tenant.setUsername("elenad");
        tenant.setPassword("tenant123");
        tenant.setPhone("0723456789");
        tenant.setAddress("Strada Avram Iancu 18, Cluj-Napoca");
        tenant.setProfilePictureUrl("/assets/profile-elena.jpg");
        tenant.setRole(User.UserRole.TENANT);

        // Create an admin user
        User admin = new User();
        admin.setId(3L);
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setPhone("0712345678");
        admin.setAddress("Strada Administrației 1, București");
        admin.setProfilePictureUrl("/assets/profile-admin.jpg");
        admin.setRole(User.UserRole.ADMIN);

        // Add all users to repository
        userRepository.save(owner);
        userRepository.save(tenant);
        userRepository.save(admin);
    }
}