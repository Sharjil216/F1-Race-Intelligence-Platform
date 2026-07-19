package com.sharjil.f1intel.ingestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeetingIngestionServiceIT extends AbstractIngestionServiceIT {

    private static final int EXPECTED_MEETINGS = 25;

    @Autowired MeetingIngestionService meetingIngestionService;

    @BeforeEach
    void stubMeetingsEndpoint() throws Exception {
        stubEndpoint("meetings-2024.json", "meetings");
    }

    @Test
    void ingestMeetingsAndIsIdempotent() {
        int ingestedMeetings = meetingIngestionService.ingestMeetings(2024);

        assertEquals(EXPECTED_MEETINGS, ingestedMeetings);
        assertEquals(25, countRows("meeting"));

        meetingIngestionService.ingestMeetings(2024);
        assertEquals(25, countRows("meeting"));
    }
}
