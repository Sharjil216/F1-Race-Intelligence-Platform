package com.sharjil.f1intel.engine.model;

import java.math.BigDecimal;
import java.util.List;

public record PitLossSummary(
        BigDecimal medianPitLoss,
        int stopCount,
        List<PitLossStop> stops) {}
