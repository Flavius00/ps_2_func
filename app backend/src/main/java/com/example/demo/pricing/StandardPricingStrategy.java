package com.example.demo.pricing;

import com.example.demo.model.ComercialSpace;
import org.springframework.stereotype.Component;

@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculateMonthlyPrice(ComercialSpace space, int contractMonths) {
        return space.getPricePerMonth(); // Preț normal
    }

    @Override
    public String getStrategyName() {
        return "standard";
    }
}