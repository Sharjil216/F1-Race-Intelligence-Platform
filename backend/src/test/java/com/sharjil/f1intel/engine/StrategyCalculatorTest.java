package com.sharjil.f1intel.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrategyCalculatorTest {

    private final StrategyCalculator strategyCalculator = new StrategyCalculator();

    @Test
    public void testTotalCost() {


        assertEquals(13.233, strategyCalculator.totalCost(0.0256, 0.0150, 53, 15), 0.001);
        assertEquals(14.697, strategyCalculator.totalCost(0.0256, 0.0150, 53, 10), 0.001);
        assertEquals(12.784, strategyCalculator.totalCost(0.0256, 0.0150, 53, 20), 0.001);
    }

    @Test
    public void testOptimalStopLap() {
        assertEquals(20, strategyCalculator.optimalStopLap(0.0256, 0.0150, 53));
    }
}
