package com.sharjil.f1intel.engine.model;

import java.math.BigDecimal;

public record StrategyResult(
        Integer driverNumber,
        Integer totalLaps,
        String firstCompound,
        String secondCompound,
        BigDecimal firstSlope,
        BigDecimal secondSlope,
        BigDecimal firstR2,
        BigDecimal secondR2,
        Integer actualStopLap,
        Integer optimalStopLap,
        double actualCost,
        double optimalCost,
        double timeDeltaSeconds) {}
