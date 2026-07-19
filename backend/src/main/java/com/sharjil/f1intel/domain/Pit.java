package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record Pit(@JsonProperty("session_key") Integer sessionKey,
                  @JsonProperty("driver_number") Integer driverNumber,
                  @JsonProperty("lap_number") Integer lapNumber,
                  @JsonProperty("pit_time") Instant pitTime,
                  @JsonProperty("stop_duration") BigDecimal stopDuration,
                  @JsonProperty("pit_duration") BigDecimal pitDuration,
                  @JsonProperty("lane_duration") BigDecimal laneDuration) {
}
