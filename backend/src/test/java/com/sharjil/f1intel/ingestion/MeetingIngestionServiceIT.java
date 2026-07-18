package com.sharjil.f1intel.ingestion;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sharjil.f1intel.TestcontainersConfiguration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MeetingIngestionServiceIT {

    static WireMockServer wireMock;

    @Autowired MeetingIngestionService meetingIngestionService;
    @Autowired JdbcClient jdbcClient;

    @BeforeAll
    static void startWireMock() {
        wireMock = new WireMockServer(0);
        wireMock.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @DynamicPropertySource
    static void overrideBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("openf1.base-url", () -> "http://localhost:" + wireMock.port() + "/v1");
    }

    @BeforeEach
    void stubMeetingsEndpoint() throws Exception {
        String body = new ClassPathResource("fixtures/meetings-2024.json")
                .getContentAsString(StandardCharsets.UTF_8);

        wireMock.stubFor(get(urlPathEqualTo("/v1/meetings"))
                .willReturn(okJson(body)));
    }

    @Test
    void ingestMeetingsAndIsIdempotent() {
        int ingestedMeetings = meetingIngestionService.ingestMeetings(2024);

        assertEquals(25, ingestedMeetings);

        long count = jdbcClient.sql("SELECT COUNT(*) FROM meeting")
                .query(Long.class)
                .single();

        assertEquals(25, count);

        meetingIngestionService.ingestMeetings(2024);

        long secondCount = jdbcClient.sql("SELECT COUNT(*) FROM meeting")
                .query(Long.class)
                .single();

        assertEquals(25, secondCount);
    }
}
