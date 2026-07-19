package com.sharjil.f1intel.ingestion;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sharjil.f1intel.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class PitIngestionServiceIT {

    private static final int EXPECTED_PITS = 45;

    private static final int[] DRIVER_NUMBERS = {
            1, 2, 3, 4, 10, 11, 14, 16, 18, 20,
            22, 23, 24, 27, 31, 44, 55, 63, 77, 81
    };

    static WireMockServer wireMock;

    @Autowired PitIngestionService pitIngestionService;
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
    void stubPitEndpoint() throws Exception {
        String body = new ClassPathResource("fixtures/pit-9558.json")
                .getContentAsString(StandardCharsets.UTF_8);

        wireMock.stubFor(get(urlPathEqualTo("/v1/pit"))
                .willReturn(okJson(body)));
    }

    @BeforeEach
    void seedParentRows() {
        jdbcClient.sql("""
                INSERT INTO meeting (meeting_key, year, meeting_name)
                VALUES (1240, 2024, 'British Grand Prix')
                ON CONFLICT (meeting_key) DO NOTHING
                """).update();

        jdbcClient.sql("""
                INSERT INTO session (session_key, meeting_key, session_name, session_type)
                VALUES (9558, 1240, 'Race', 'Race')
                ON CONFLICT (session_key) DO NOTHING
                """).update();

        for (int driverNumber : DRIVER_NUMBERS) {
            jdbcClient.sql("""
                INSERT INTO driver (session_key, driver_number, full_name, team_name)
                VALUES (9558, :driverNumber, 'Test Driver', 'Test Team')
                ON CONFLICT (session_key, driver_number) DO NOTHING
                """)
                .param("driverNumber", driverNumber)
                .update();
        }
    }

    @Test
    void ingestsPitStopsAndIsIdempotent() {
        int ingested = pitIngestionService.ingestPits(9558);
        assertEquals(EXPECTED_PITS, ingested);

        assertEquals(EXPECTED_PITS, countPits());

        pitIngestionService.ingestPits(9558);
        assertEquals(EXPECTED_PITS, countPits());
    }

    private long countPits() {
        return jdbcClient.sql("SELECT COUNT(*) FROM pit")
                .query(Long.class)
                .single();
    }
}