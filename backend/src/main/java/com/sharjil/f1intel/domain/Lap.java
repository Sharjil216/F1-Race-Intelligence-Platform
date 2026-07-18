package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record Lap(@JsonProperty("lap_number") Integer lapNumber,
                  @JsonProperty("driver_number") Integer driverNumber,
                  @JsonProperty("session_key") Integer sessionKey,
                  @JsonProperty("date_start") Instant dateStart,
                  @JsonProperty("lap_duration") BigDecimal lapDuration,
                  @JsonProperty("duration_sector_1") BigDecimal durationSector1,
                  @JsonProperty("duration_sector_2") BigDecimal durationSector2,
                  @JsonProperty("duration_sector_3") BigDecimal durationSector3,
                  @JsonProperty("i1_speed") Integer i1Speed,
                  @JsonProperty("i2_speed") Integer i2Speed,
                  @JsonProperty("st_speed") Integer stSpeed,
                  @JsonProperty("is_pit_out_lap") Boolean isPitOutLap) {

    public OffsetDateTime dateStartOffset() {
        return dateStart == null ? null : dateStart.atOffset(ZoneOffset.UTC);
    }
}
