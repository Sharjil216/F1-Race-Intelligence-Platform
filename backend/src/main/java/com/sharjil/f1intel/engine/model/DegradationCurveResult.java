package com.sharjil.f1intel.engine.model;

import java.math.BigDecimal;

public record DegradationCurveResult(String compound, Integer ageBucket, Integer laps, BigDecimal avgDelta) {
}
