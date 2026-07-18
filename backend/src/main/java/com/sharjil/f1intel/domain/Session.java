package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record Session(@Id @JsonProperty("session_key") Integer sessionKey,
                      @JsonProperty("meeting_key") Integer meetingKey,
                      @JsonProperty("session_name") String sessionName,
                      @JsonProperty("session_type") String sessionType,
                      @JsonProperty("date_start") Instant startTime,
                      @JsonProperty("date_end") Instant endTime) {

    public OffsetDateTime startTimeOffset() {
        return startTime == null ? null : startTime.atOffset(ZoneOffset.UTC);
    }
    public OffsetDateTime endTimeOffset() {
        return endTime == null ? null : endTime.atOffset(ZoneOffset.UTC);
    }
}
