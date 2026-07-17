package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record Meeting(@Id @JsonProperty("meeting_key") Integer meetingKey,
                      @JsonProperty("year") Integer year,
                      @JsonProperty("meeting_name") String meetingName,
                      @JsonProperty("meeting_official_name") String meetingOfficialName,
                      @JsonProperty("location") String location,
                      @JsonProperty("country_name") String countryName,
                      @JsonProperty("circuit_short_name") String circuitShortName,
                      @JsonProperty("date_start") Instant dateStart,
                      @JsonProperty("date_end") Instant dateEnd) implements Persistable<Integer> {

    public OffsetDateTime dateStartOffset() {
        return dateStart == null ? null : dateStart.atOffset(ZoneOffset.UTC);
    }

    public OffsetDateTime dateEndOffset() {
        return dateEnd == null ? null : dateEnd.atOffset(ZoneOffset.UTC);
    }

    @Override
    public @Nullable Integer getId() {
        return meetingKey;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
