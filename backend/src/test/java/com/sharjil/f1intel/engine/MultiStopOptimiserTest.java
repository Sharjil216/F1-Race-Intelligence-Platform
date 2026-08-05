package com.sharjil.f1intel.engine;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiStopOptimiserTest {

    @Test
    public void doesNotStopWhenPitLossOutweighsTyreSaving() {
        MultiStopOptimiser optimiser = new MultiStopOptimiser(10, 20.0, Map.of("A", 0.05, "B", 0.02));

        assertEquals(0.90, optimiser.bestCost(1), 0.001);
    }

    @Test
    public void stopsWhenTyreSavingBeatsPitLoss() {
        MultiStopOptimiser optimiser = new MultiStopOptimiser(
                20, 2.0, Map.of("A", 0.05));

        assertEquals(6.50, optimiser.bestCost(1), 0.001);
    }

    @Test
    public void testCall() {
        MultiStopOptimiser optimiser = new MultiStopOptimiser(66, 20.0, Map.of("A", 0.05, "B", 0.02));

        System.out.println(optimiser.bestCost(3));
    }
}
