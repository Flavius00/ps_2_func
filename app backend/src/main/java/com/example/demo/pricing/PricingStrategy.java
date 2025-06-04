package com.example.demo.pricing;

import com.example.demo.model.ComercialSpace;

public interface PricingStrategy {
    double calculateMonthlyPrice(ComercialSpace space, int contractMonths);
    String getStrategyName();
}