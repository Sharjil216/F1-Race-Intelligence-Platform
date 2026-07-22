package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Position(@JsonProperty("session_key") Integer sessionKey,
                       @JsonProperty("driver_number") Integer driverNumber,
                       @JsonProperty("position") Integer position,
                       @JsonProperty("date") Instant date) {
}
