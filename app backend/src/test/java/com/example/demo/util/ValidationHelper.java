package com.example.demo.util;

import com.example.demo.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ValidationHelper {

    /**
     * Convertește constraint violations în format user-friendly pentru frontend
     */
    public static Map<String, Object> formatValidationErrors(Set<ConstraintViolation<?>> violations) {
        Map<String, Object> result = new HashMap<>();

        Map<String, String> fieldErrors = violations.stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        result.put("hasErrors", !fieldErrors.isEmpty());
        result.put("errorCount", fieldErrors.size());
        result.put("fieldErrors", fieldErrors);

        return result;
    }

    /**
     * Creează un mesaj de eroare user-friendly
     */
    public static String createUserFriendlyMessage(Map<String, String> errors) {
        if (errors.isEmpty()) {
            return "Datele sunt valide.";
        }

        if (errors.size() == 1) {
            return "A fost găsită o eroare de validare: " + errors.values().iterator().next();
        }

        return "Au fost găsite " + errors.size() + " erori de validare. " +
                "Verificați câmpurile marcate și încercați din nou.";
    }

    /**
     * Validează un obiect și aruncă excepție dacă nu este valid
     */
    public static <T> void validateAndThrow(T object, Validator validator, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);

        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));

            throw new ValidationException("Validation failed", errors);
        }
    }

    /**
     * Verifică dacă un string reprezintă un număr de telefon valid românesc
     */
    public static boolean isValidRomanianPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Let @NotBlank handle empty validation
        }

        String cleanPhone = phone.replaceAll("[\\s-]", "");
        return cleanPhone.matches("^(\\+40|0040|0)?([1-9][0-9]{8})$");
    }

    /**
     * Verifică dacă un string reprezintă un cod fiscal românesc valid
     */
    public static boolean isValidRomanianTaxId(String taxId) {
        if (taxId == null || taxId.trim().isEmpty()) {
            return true; // Let @NotBlank handle empty validation
        }

        return taxId.trim().toUpperCase().matches("^RO[0-9]{2,10}$");
    }

    /**
     * Verifică dacă coordonatele sunt în România
     */
    public static boolean areCoordinatesInRomania(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return true; // Let @NotNull handle null validation
        }

        return latitude >= 43.5 && latitude <= 48.5 &&
                longitude >= 20.0 && longitude <= 30.0;
    }

    /**
     * Calculează distanța între două puncte geografice (în grade)
     */
    public static double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return Double.MAX_VALUE;
        }

        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2));
    }
}