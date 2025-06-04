package com.example.demo.performance;

import com.example.demo.dto.ComercialSpaceCreateDto;
import com.example.demo.service.ValidationService;
import com.example.demo.validation.groups.ValidationGroups.CreateValidation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ValidationPerformanceTest {

    @Autowired
    private ValidationService validationService;

    @Test
    void shouldValidateQuickly() {
        ComercialSpaceCreateDto dto = createValidDto();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Validează de 1000 de ori
        for (int i = 0; i < 1000; i++) {
            Map<String, String> errors = validationService.validateObject(dto, CreateValidation.class);
            assertThat(errors).isEmpty();
        }

        stopWatch.stop();

        long totalTimeMs = stopWatch.getTotalTimeMillis();
        double avgTimeMs = totalTimeMs / 1000.0;

        System.out.println("Total validation time for 1000 objects: " + totalTimeMs + "ms");
        System.out.println("Average validation time per object: " + avgTimeMs + "ms");

        // Validarea unui obiect nu ar trebui să dureze mai mult de 10ms în medie
        assertThat(avgTimeMs).isLessThan(10.0);
    }

    @Test
    void shouldValidateInvalidObjectsQuickly() {
        ComercialSpaceCreateDto invalidDto = createInvalidDto();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Validează de 1000 de ori obiecte invalide
        for (int i = 0; i < 1000; i++) {
            Map<String, String> errors = validationService.validateObject(invalidDto, CreateValidation.class);
            assertThat(errors).isNotEmpty();
        }

        stopWatch.stop();

        long totalTimeMs = stopWatch.getTotalTimeMillis();
        double avgTimeMs = totalTimeMs / 1000.0;

        System.out.println("Total validation time for 1000 invalid objects: " + totalTimeMs + "ms");
        System.out.println("Average validation time per invalid object: " + avgTimeMs + "ms");

        // Chiar și validarea obiectelor invalide nu ar trebui să dureze mult
        assertThat(avgTimeMs).isLessThan(15.0);
    }

    private ComercialSpaceCreateDto createValidDto() {
        return ComercialSpaceCreateDto.builder()
                .name("Test Office Space")
                .description("Modern office space")
                .area(150.0)
                .pricePerMonth(2000.0)
                .address("Strada Test 123, Cluj-Napoca")
                .latitude(46.7712)
                .longitude(23.6236)
                .amenities(Arrays.asList("AC", "Internet", "Parking"))
                .available(true)
                .spaceType("OFFICE")
                .ownerId(1L)
                .buildingId(1L)
                .numberOfRooms(4)
                .hasReception(true)
                .build();
    }

    private ComercialSpaceCreateDto createInvalidDto() {
        return ComercialSpaceCreateDto.builder()
                .name("") // Invalid
                .area(-10.0) // Invalid
                .pricePerMonth(50.0) // Invalid
                .latitude(60.0) // Invalid
                .longitude(10.0) // Invalid
                .spaceType("INVALID") // Invalid
                .build();
    }
}