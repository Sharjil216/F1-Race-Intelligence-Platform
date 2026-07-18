package com.sharjil.f1intel.api;

import com.sharjil.f1intel.ingestion.DriverIngestionService;
import com.sharjil.f1intel.ingestion.LapIngestionService;
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
    private final DriverIngestionService driverIngestionService;
    private final LapIngestionService lapIngestionService;

    public IngestionController(MeetingIngestionService meetingIngestionService, SessionIngestionService sessionIngestionService, DriverIngestionService driverIngestionService, LapIngestionService lapIngestionService) {
        this.meetingIngestionService = meetingIngestionService;
        this.sessionIngestionService = sessionIngestionService;
        this.driverIngestionService = driverIngestionService;
        this.lapIngestionService = lapIngestionService;
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

    @PostMapping("/drivers")
    public IngestionResult ingestDrivers(int sessionKey) {
        int ingested = driverIngestionService.ingestDrivers(sessionKey);

        return new IngestionResult("drivers", ingested);
    }

    @PostMapping("/laps")
    public IngestionResult ingestLaps(int sessionKey) {
        int ingested = lapIngestionService.ingestLaps(sessionKey);

        return new IngestionResult("laps", ingested);
    }

    public record IngestionResult(String endpoint, int ingested) {}
}
