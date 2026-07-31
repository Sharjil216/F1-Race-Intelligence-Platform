package com.sharjil.f1intel.engine;

public class StrategyCalculator {

    public double stintCost(double k, int length) {
        return k * length * (length - 1) / 2;
    }

    public double totalCost(double kFirst, double kSecond, int totalLaps, int stopLap) {
        return stintCost(kFirst, stopLap) + stintCost(kSecond, totalLaps - stopLap);
    }

    public int optimalStopLap(double kFirst, double kSecond, int totalLaps) {
        if (totalLaps < 2) {
            throw new IllegalArgumentException();
        }

        double bestCost = Double.MAX_VALUE;
        int bestLap = -1;

        for (int stopLap = 1; stopLap < totalLaps; stopLap++) {
            double cost = totalCost(kFirst, kSecond, totalLaps, stopLap);

            if (cost < bestCost) {
                bestCost = cost;
                bestLap = stopLap;
            }
        }

        return bestLap;
    }
}
