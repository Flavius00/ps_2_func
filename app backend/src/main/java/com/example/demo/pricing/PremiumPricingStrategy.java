package com.example.demo.pricing;

import com.example.demo.model.ComercialSpace;
import org.springframework.stereotype.Component;

@Component
public class PremiumPricingStrategy implements PricingStrategy {

    @Override
    public double calculateMonthlyPrice(ComercialSpace space, int contractMonths) {
        double basePrice = space.getPricePerMonth();

        // Preț premium pentru spații de lux (cu multe amenities)
        if (space.getAmenities() != null && space.getAmenities().size() > 5) {
            return basePrice * 1.15; // +15% pentru spații premium
        }

        return basePrice * 1.05; // +5% pentru servicii premium
    }

    @Override
    public String getStrategyName() {
        return "premium";
    }
}