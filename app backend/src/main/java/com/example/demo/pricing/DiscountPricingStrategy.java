package com.example.demo.pricing;

import com.example.demo.model.ComercialSpace;
import org.springframework.stereotype.Component;

@Component
public class DiscountPricingStrategy implements PricingStrategy {

    @Override
    public double calculateMonthlyPrice(ComercialSpace space, int contractMonths) {
        double basePrice = space.getPricePerMonth();

        // Discount pentru contracte lungi
        if (contractMonths >= 24) {
            return basePrice * 0.9; // 10% discount pentru 2+ ani
        } else if (contractMonths >= 12) {
            return basePrice * 0.95; // 5% discount pentru 1+ ani
        }

        return basePrice;
    }


    @Override
    public String getStrategyName() {
        return "discount";
    }
}
