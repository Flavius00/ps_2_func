package com.example.demo.service;

import com.example.demo.model.ComercialSpace;
import com.example.demo.pricing.PricingStrategy;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PriceCalculatorService {

    private final Map<String, PricingStrategy> strategies;

    public PriceCalculatorService(List<PricingStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        PricingStrategy::getStrategyName,
                        Function.identity()
                ));
    }

    public double calculatePrice(ComercialSpace space, int contractMonths, String strategyType) {
        PricingStrategy strategy = strategies.getOrDefault(strategyType, strategies.get("standard"));
        return strategy.calculateMonthlyPrice(space, contractMonths);
    }

    public double calculateTotalPrice(ComercialSpace space, int contractMonths, String strategyType) {
        double monthlyPrice = calculatePrice(space, contractMonths, strategyType);
        return monthlyPrice * contractMonths;
    }

    // Helper pentru frontend
    public Map<String, Double> getAllPriceOptions(ComercialSpace space, int contractMonths) {
        return strategies.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().calculateMonthlyPrice(space, contractMonths)
                ));
    }
}