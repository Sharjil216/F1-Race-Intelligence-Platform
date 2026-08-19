package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.Stint;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiStopOptimiserTest {

    @Test
    public void doesNotStopWhenPitLossOutweighsTyreSaving() {
        MultiStopOptimiser optimiser = new MultiStopOptimiser(10, 20.0, Map.of("A", 0.05, "B", 0.02));

        assertEquals(0.90, optimiser.bestCost(1).cost(), 0.001);
    }

    @Test
    public void stopsWhenTyreSavingBeatsPitLoss() {
        MultiStopOptimiser optimiser = new MultiStopOptimiser(
                20, 2.0, Map.of("A", 0.05));

        MultiStopOptimiser.Result result = optimiser.bestCost(1);
        System.out.println("Best cost stints: " + result.stints());
        assertEquals(6.50, result.cost(), 0.001);
    }

    @Test
    public void costsAnActualStrategyCorrectly() {
        MultiStopOptimiser optimiser = new MultiStopOptimiser(
                53, 24.7, Map.of("MEDIUM", 0.0256, "HARD", 0.0150));

        List<Stint> actual = List.of(
                new Stint("MEDIUM", 1, 15),
                new Stint("HARD", 16, 53)
        );

        assertEquals(37.933, optimiser.costOfStrategy(actual), 0.01);
    }
}
