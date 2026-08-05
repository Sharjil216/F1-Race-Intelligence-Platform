package com.sharjil.f1intel.engine;

import java.util.HashMap;
import java.util.Map;

public class MultiStopOptimiser {

    private final int totalLaps;
    private final double pitLoss;
    private final Map<String, Double> slopes;

    private final Map<String, Double> cache = new HashMap<>();

    public MultiStopOptimiser(int totalLaps, double pitLoss, Map<String, Double> slopes) {
        this.totalLaps = totalLaps;
        this.pitLoss = pitLoss;
        this.slopes = slopes;
    }

    private double stintCost(double k, int length) {
        return k * length * (length - 1) / 2;
    }

    private double bestCostFrom(int startLap, int stopsRemaining) {

        String key = startLap + ":" + stopsRemaining;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        double best = Double.MAX_VALUE;

        for (double k : slopes.values()) {

            int lengthToEnd = totalLaps - startLap + 1;
            double costToEnd = stintCost(k, lengthToEnd);

            if (costToEnd < best) {
                best = costToEnd;
            }

            if (stopsRemaining  > 0) {
                for (int e = startLap; e < totalLaps; e++) {
                    int startLength = e - startLap + 1;
                    double thisStint = stintCost(k, startLength);
                    double rest = bestCostFrom(e + 1, stopsRemaining - 1);
                    double total = thisStint + pitLoss + rest;

                    if (total < best) {
                        best = total;
                    }
                }
            }
        }

        cache.put(key, best);
        return best;
    }

    public double bestCost(int maxStops) {
        return  bestCostFrom(1, maxStops);
    }
}
