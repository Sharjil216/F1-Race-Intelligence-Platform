package com.sharjil.f1intel.api;

import com.sharjil.f1intel.ingestion.MeetingIngestionService;
import com.sharjil.f1intel.ingestion.SessionIngestionService;
import com.sharjil.f1intel.repository.MeetingRepository;
import com.sharjil.f1intel.repository.SessionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final MeetingIngestionService meetingIngestionService;
    private final SessionIngestionService sessionIngestionService;

    public IngestionController(MeetingIngestionService meetingIngestionService, MeetingRepository meetingRepository, SessionIngestionService sessionIngestionService, SessionRepository sessionRepository) {
        this.meetingIngestionService = meetingIngestionService;
        this.sessionIngestionService = sessionIngestionService;
    }

    @PostMapping("/meetings")
    public IngestionResult ingestMeeting(int year) {
        int ingested = meetingIngestionService.ingestMeetings(year);

        return new IngestionResult("meetings", ingested);
    }

    @PostMapping("/sessions")
    public IngestionResult ingestSession(int meetingKey) {
        int ingested = sessionIngestionService.ingestSessions(meetingKey);

        return new IngestionResult("sessions", ingested);
    }

    public record IngestionResult(String endpoint, int ingested) {}
}
