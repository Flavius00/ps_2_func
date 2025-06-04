package com.example.demo.validation.validator;

import com.example.demo.validation.annotation.ValidCoordinates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class CoordinatesValidator implements ConstraintValidator<ValidCoordinates, Object> {

    // Romania boundaries (approximate)
    private static final double MIN_LATITUDE = 43.5;
    private static final double MAX_LATITUDE = 48.5;
    private static final double MIN_LONGITUDE = 20.0;
    private static final double MAX_LONGITUDE = 30.0;

    @Override
    public void initialize(ValidCoordinates constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            Field latitudeField = obj.getClass().getDeclaredField("latitude");
            Field longitudeField = obj.getClass().getDeclaredField("longitude");

            latitudeField.setAccessible(true);
            longitudeField.setAccessible(true);

            Double latitude = (Double) latitudeField.get(obj);
            Double longitude = (Double) longitudeField.get(obj);

            if (latitude == null || longitude == null) {
                return true; // Let @NotNull handle null validation
            }

            boolean latValid = latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE;
            boolean lngValid = longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;

            if (!latValid || !lngValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Coordinates must be valid for Romania"
                ).addConstraintViolation();
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
