package com.example.demo.service.impl;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PasswordMigrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Verifică statusul parolelor din baza de date
     */
    @Transactional(readOnly = true)
    public void checkPasswordStatus() {
        List<User> allUsers = userRepository.findAll();

        System.out.println("=".repeat(60));
        System.out.println("📊 PASSWORD STATUS REPORT");
        System.out.println("=".repeat(60));

        Map<String, List<User>> statusGroups = allUsers.stream()
                .collect(Collectors.groupingBy(user -> isPasswordEncrypted(user.getPassword()) ? "ENCRYPTED" : "PLAIN_TEXT"));

        statusGroups.forEach((status, users) -> {
            System.out.println(String.format("🔐 %s: %d users", status, users.size()));
            users.forEach(user ->
                    System.out.println(String.format("   - ID: %d, Username: %s", user.getId(), user.getUsername()))
            );
        });

        System.out.println("=".repeat(60));
    }

    /**
     * Verifică dacă o parolă este deja criptată
     */
    private boolean isPasswordEncrypted(String password) {
        return password != null && (
                password.startsWith("$2a$") ||
                        password.startsWith("$2b$") ||
                        password.startsWith("$2y$")
        );
    }

    /**
     * Migrează parolele necriptate (FOLOSEȘTE CU ATENȚIE!)
     * Această metodă va cripta parolele care sunt în text simplu
     */
    public void migrateUnencryptedPasswords() {
        List<User> allUsers = userRepository.findAll();
        int migratedCount = 0;

        System.out.println("🔄 Starting password migration...");

        for (User user : allUsers) {
            if (!isPasswordEncrypted(user.getPassword())) {
                String originalPassword = user.getPassword();
                String encryptedPassword = passwordEncoder.encode(originalPassword);
                user.setPassword(encryptedPassword);
                userRepository.save(user);
                migratedCount++;

                System.out.println(String.format("✅ Migrated password for user: %s (ID: %d)",
                        user.getUsername(), user.getId()));
            }
        }

        System.out.println(String.format("🎉 Migration completed! %d passwords encrypted.", migratedCount));
    }

    /**
     * Resetează toate parolele la o valoare temporară
     * Utilizatorii vor trebui să își schimbe parolele
     */
    public void resetAllPasswordsToTemporary() {
        List<User> allUsers = userRepository.findAll();
        String temporaryPassword = "TempPass123!";
        String encryptedTempPassword = passwordEncoder.encode(temporaryPassword);

        System.out.println("⚠️  Resetting all passwords to temporary value...");
        System.out.println("📧 Users will need to reset their passwords!");

        for (User user : allUsers) {
            user.setPassword(encryptedTempPassword);
            userRepository.save(user);
            System.out.println(String.format("🔄 Reset password for: %s", user.getUsername()));
        }

        System.out.println("✅ All passwords reset to: " + temporaryPassword);
        System.out.println("📧 Notify users to change their passwords!");
    }
}