package com.sharjil.f1intel.engine.model;

import java.math.BigDecimal;

public record PitLossStop(
        Integer driverNumber,
        Integer inLap,
        Integer outLap,
        BigDecimal inLapDuration,
        BigDecimal outLapDuration,
        BigDecimal referenceLapDuration,
        BigDecimal inLapLoss,
        BigDecimal outLapLoss,
        BigDecimal totalPitLoss) {}
