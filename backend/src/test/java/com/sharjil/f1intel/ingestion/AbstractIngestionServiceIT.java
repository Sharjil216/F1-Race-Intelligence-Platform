package com.sharjil.f1intel.ingestion;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sharjil.f1intel.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIngestionServiceIT {

    private static final int[] DRIVER_NUMBERS = {
            1, 2, 3, 4, 10, 11, 14, 16, 18, 20,
            22, 23, 24, 27, 31, 44, 55, 63, 77, 81
    };

    protected static final WireMockServer wireMock = new WireMockServer(0);

    static {
        wireMock.start();
    }

    @Autowired protected JdbcClient jdbcClient;

    @DynamicPropertySource
    static void overrideBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("openf1.base-url", () -> "http://localhost:" + wireMock.port() + "/v1");
    }

    protected void stubEndpoint(String fileName, String stubbingPath) throws Exception {
        String body = new ClassPathResource("fixtures/" + fileName)
                .getContentAsString(StandardCharsets.UTF_8);

        wireMock.stubFor(get(urlPathEqualTo("/v1/" +  stubbingPath))
                .willReturn(okJson(body)));
    }

    @BeforeEach
    void resetStubs() {
        wireMock.resetAll();
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

    protected long countRows(String tableName) {
        return jdbcClient.sql("SELECT COUNT(*) FROM "  + tableName)
                .query(Long.class)
                .single();
    }
}
