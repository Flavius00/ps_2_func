package com.example.demo.aspect;

import com.example.demo.exception.ValidationException;
import com.example.demo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Aspect
@Component
public class ValidationAspect {

    private final ValidationService validationService;

    public ValidationAspect(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Around("@annotation(com.example.demo.annotation.ValidateInput)")
    public Object validateInput(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg != null) {
                Map<String, String> errors = validationService.validateObject(arg);
                if (!errors.isEmpty()) {
                    log.warn("Validation failed in {}: {}",
                            joinPoint.getSignature().getName(), errors);
                    throw new ValidationException("Input validation failed", errors);
                }
            }
        }

        return joinPoint.proceed();
    }
}
