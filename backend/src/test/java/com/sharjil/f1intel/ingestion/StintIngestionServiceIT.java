package com.sharjil.f1intel.ingestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StintIngestionServiceIT extends AbstractIngestionServiceIT {

    private static final int EXPECTED_STINTS = 65;

    @Autowired StintIngestionService stintIngestionService;

    @BeforeEach
    void stubStintsEndpoint() throws Exception {
        stubEndpoint("stints-9558.json", "stints");
    }

    @Test
    void ingestsStintsAndIsIdempotent() {
        int ingested = stintIngestionService.ingestStints(9558);
        assertEquals(EXPECTED_STINTS, ingested);

        assertEquals(EXPECTED_STINTS, countRows("stint"));

        stintIngestionService.ingestStints(9558);
        assertEquals(EXPECTED_STINTS, countRows("stint"));
    }

}