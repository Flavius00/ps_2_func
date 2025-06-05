package com.example.demo.controller;

import com.example.demo.service.impl.PasswordMigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/migration")
@CrossOrigin(origins = "http://localhost:3000")
public class MigrationController {

    private final PasswordMigrationService migrationService;

    public MigrationController(PasswordMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    /**
     * Verifică statusul parolelor - SAFE să rulezi
     */
    @GetMapping("/check-passwords")
    public ResponseEntity<?> checkPasswordStatus() {
        try {
            migrationService.checkPasswordStatus();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password status check completed. Check console logs for details.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error checking password status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Migrează parolele necriptate - ATENȚIE!
     */
    @PostMapping("/migrate-passwords")
    public ResponseEntity<?> migratePasswords(@RequestParam(defaultValue = "false") boolean confirmMigration) {
        if (!confirmMigration) {
            Map<String, Object> warningResponse = new HashMap<>();
            warningResponse.put("success", false);
            warningResponse.put("message", "Migration not confirmed. Add ?confirmMigration=true to proceed.");
            warningResponse.put("warning", "This will encrypt all plain text passwords!");
            return ResponseEntity.badRequest().body(warningResponse);
        }

        try {
            migrationService.migrateUnencryptedPasswords();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password migration completed successfully!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error during migration: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Resetează toate parolele - FOARTE ATENȚIE!
     */
    @PostMapping("/reset-all-passwords")
    public ResponseEntity<?> resetAllPasswords(@RequestParam(defaultValue = "false") boolean confirmReset) {
        if (!confirmReset) {
            Map<String, Object> warningResponse = new HashMap<>();
            warningResponse.put("success", false);
            warningResponse.put("message", "Reset not confirmed. Add ?confirmReset=true to proceed.");
            warningResponse.put("warning", "This will reset ALL user passwords to a temporary value!");
            return ResponseEntity.badRequest().body(warningResponse);
        }

        try {
            migrationService.resetAllPasswordsToTemporary();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All passwords reset to temporary value: TempPass123!");
            response.put("action_required", "Notify all users to change their passwords!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error during reset: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}