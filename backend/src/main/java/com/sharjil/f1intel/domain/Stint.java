package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stint(@JsonProperty("stint_number") Integer stintNumber,
                    @JsonProperty("session_key") Integer sessionKey,
                    @JsonProperty("driver_number") Integer driverNumber,
                    @JsonProperty("lap-start") Integer lapStart,
                    @JsonProperty("lap-end") Integer lapEnd,
                    @JsonProperty("compound") String compound,
                    @JsonProperty("tyre_age_at_start") Integer tyreAgeAtStart) {
}
