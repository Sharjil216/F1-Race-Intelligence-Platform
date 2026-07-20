package com.sharjil.f1intel.engine.model;

import java.math.BigDecimal;

public record RaceStateResult(Integer position, Integer driverNumber, BigDecimal gapToLeader, BigDecimal gapToAhead) {
}
