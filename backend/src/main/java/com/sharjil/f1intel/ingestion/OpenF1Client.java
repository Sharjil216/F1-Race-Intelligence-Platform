package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Meeting;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OpenF1Client {

    private final RestClient restClient;

    public OpenF1Client(RestClient openF1RestClient) {
        this.restClient = openF1RestClient;
    }

    public List<Meeting> fetchMeetings(int year) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/meetings")
                        .queryParam("year", year)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Meeting>>() {});
    }
}
