package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.Stint;
import com.sharjil.f1intel.exception.UnsupportedStrategyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiStopOptimiser {

    private final int totalLaps;
    private final double pitLoss;
    private final Map<String, Double> slopes;

    private final Map<String, Result> cache = new HashMap<>();

    public MultiStopOptimiser(int totalLaps, double pitLoss, Map<String, Double> slopes) {
        this.totalLaps = totalLaps;
        this.pitLoss = pitLoss;
        this.slopes = slopes;
    }

    private double stintCost(double k, int length) {
        return k * length * (length - 1) / 2;
    }

    private Result bestCostFrom(int startLap, int stopsRemaining) {

        String key = startLap + ":" + stopsRemaining;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Result best = null;

        for (Map.Entry<String, Double> entry : slopes.entrySet()) {

            String compound = entry.getKey();
            double k = entry.getValue();

            int lengthToEnd = totalLaps - startLap + 1;
            double costToEnd = stintCost(k, lengthToEnd);

            if (best == null || costToEnd < best.cost()) {
                List<Stint> stints = List.of(new Stint(compound, startLap, totalLaps));
                best = new Result(costToEnd, stints);
            }

            if (stopsRemaining  > 0) {
                for (int e = startLap; e < totalLaps; e++) {
                    int startLength = e - startLap + 1;
                    double thisStint = stintCost(k, startLength);
                    Result rest = bestCostFrom(e + 1, stopsRemaining - 1);
                    double total = thisStint + pitLoss + rest.cost();

                    if (best == null || total < best.cost()) {
                        List<Stint> stints = new ArrayList<>();
                        stints.add(new Stint(compound, startLap, e));
                        stints.addAll(rest.stints());

                        best = new Result(total, stints);
                    }
                }
            }
        }

        cache.put(key, best);
        return best;
    }

    public Result bestCost(int maxStops) {
        return bestCostFrom(1, maxStops);
    }

    public double costOfStrategy(List<Stint> stints) {
        double total = 0;
        for (Stint stint : stints) {

            if (!slopes.containsKey(stint.compound())) {
                throw new UnsupportedStrategyException("Too few laps to determine slopes for compound " + stint.compound());
            }

            double slope = slopes.get(stint.compound());
            int startLap = stint.startLap();
            int endLap = stint.endLap();

            total += stintCost(slope, endLap - startLap + 1);
        }

        return total += (stints.size() - 1) * pitLoss;
    }

    public record Result(double cost, List<Stint> stints) {}
}
