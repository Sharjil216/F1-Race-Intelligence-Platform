package com.sharjil.f1intel.engine.model;

import java.math.BigDecimal;

public record DegradationResult(String compound, Integer lapsCounted, BigDecimal slope, BigDecimal r2) {
}
