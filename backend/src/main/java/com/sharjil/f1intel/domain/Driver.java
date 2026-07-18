package com.sharjil.f1intel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Driver(@JsonProperty("driver_number") Integer driverNumber,
                     @JsonProperty("session_key") Integer sessionKey,
                     @JsonProperty("full_name") String fullName,
                     @JsonProperty("name_acronym") String nameAcronym,
                     @JsonProperty("team_name") String teamName,
                     @JsonProperty("country_code") String countryCode,
                     @JsonProperty("team_colour") String teamColour) {
}
