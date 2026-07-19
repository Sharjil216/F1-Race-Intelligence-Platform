package com.sharjil.f1intel.ingestion;


import com.sharjil.f1intel.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class OpenF1Client {

    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    public OpenF1Client(RestClient openF1RestClient, JsonMapper openF1JsonMapper) {
        this.restClient = openF1RestClient;
        this.jsonMapper = openF1JsonMapper;
    }

    public FetchResult<Meeting> fetchMeetings(int year) {
        String raw =  restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/meetings")
                        .queryParam("year", year)
                        .build())
                .retrieve()
                .body(String.class);

        List<Meeting> parsed = jsonMapper.readValue(raw, new TypeReference<List<Meeting>>() {});

        return new FetchResult<>(raw, parsed);
    }

    public FetchResult<Session> fetchSessions(int meetingKey) {
        String raw =  restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sessions")
                        .queryParam("meeting_key", meetingKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<Session> parsed = jsonMapper.readValue(raw, new TypeReference<List<Session>>() {});

        return new FetchResult<>(raw, parsed);
    }

    public FetchResult<Driver> fetchDrivers(int sessionKey) {
        String raw =  restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/drivers")
                        .queryParam("session_key", sessionKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<Driver> parsed = jsonMapper.readValue(raw, new TypeReference<List<Driver>>() {});

        return new FetchResult<>(raw, parsed);
    }

    public FetchResult<Lap> fetchLaps(int sessionKey) {
        String raw =  restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/laps")
                        .queryParam("session_key", sessionKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<Lap> parsed = jsonMapper.readValue(raw, new TypeReference<List<Lap>>() {});

        return new FetchResult<>(raw, parsed);
    }

    public FetchResult<Stint> fetchStints(int sessionKey) {
        String raw =  restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stints")
                        .queryParam("session_key", sessionKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<Stint> parsed = jsonMapper.readValue(raw, new TypeReference<List<Stint>>() {});

        return new FetchResult<>(raw, parsed);
    }

    public FetchResult<Pit> fetchPits(int sessionKey) {
        String raw =  restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pit")
                        .queryParam("session_key", sessionKey)
                        .build())
                .retrieve()
                .body(String.class);

        List<Pit> parsed = jsonMapper.readValue(raw, new TypeReference<List<Pit>>() {});

        return new FetchResult<>(raw, parsed);
    }
}
