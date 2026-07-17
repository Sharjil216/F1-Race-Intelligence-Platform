package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.time.Instant;

public record Session(@Id @JsonProperty("session_key") Integer sessionKey,
                      @JsonProperty("meeting_key") Integer meetingKey,
                      @JsonProperty("session_name") String sessionName,
                      @JsonProperty("session_type") String sessionType,
                      @JsonProperty("date_start") Instant startTime,
                      @JsonProperty("date_end") Instant endTime) implements Persistable<Integer> {
    @Override
    public @Nullable Integer getId() {
        return sessionKey;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
