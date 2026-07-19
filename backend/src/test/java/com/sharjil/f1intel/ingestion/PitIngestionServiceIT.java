package com.sharjil.f1intel.ingestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PitIngestionServiceIT extends AbstractIngestionServiceIT {

    private static final int EXPECTED_PITS = 45;

    @Autowired PitIngestionService pitIngestionService;

    @BeforeEach
    void stubPitEndpoint() throws Exception {
        stubEndpoint("pit-9558.json", "pit");
    }

    @Test
    void ingestsPitStopsAndIsIdempotent() {
        int ingested = pitIngestionService.ingestPits(9558);
        assertEquals(EXPECTED_PITS, ingested);

        assertEquals(EXPECTED_PITS, countRows("pit"));

        pitIngestionService.ingestPits(9558);
        assertEquals(EXPECTED_PITS, countRows("pit"));
    }
}