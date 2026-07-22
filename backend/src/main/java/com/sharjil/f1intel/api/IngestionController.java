package com.sharjil.f1intel.api;

import com.sharjil.f1intel.ingestion.*;
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
    private final StintIngestionService stintIngestionService;
    private final PitIngestionService pitIngestionService;
    private final PositionIngestionService positionIngestionService;

    public IngestionController(MeetingIngestionService meetingIngestionService,
                               SessionIngestionService sessionIngestionService,
                               DriverIngestionService driverIngestionService,
                               LapIngestionService lapIngestionService,
                               StintIngestionService stintIngestionService,
                               PitIngestionService pitIngestionService,
                               PositionIngestionService positionIngestionService) {
        this.meetingIngestionService = meetingIngestionService;
        this.sessionIngestionService = sessionIngestionService;
        this.driverIngestionService = driverIngestionService;
        this.lapIngestionService = lapIngestionService;
        this.stintIngestionService = stintIngestionService;
        this.pitIngestionService = pitIngestionService;
        this.positionIngestionService = positionIngestionService;
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

    @PostMapping("/stints")
    public IngestionResult ingestStints(int sessionKey) {
        int ingested = stintIngestionService.ingestStints(sessionKey);

        return new IngestionResult("stints", ingested);
    }
    @PostMapping("/pits")
    public IngestionResult ingestPits(int sessionKey) {
        int ingested = pitIngestionService.ingestPits(sessionKey);

        return new IngestionResult("pits", ingested);
    }

    @PostMapping("/positions")
    public IngestionResult ingestPositions(int sessionKey) {
        int ingested = positionIngestionService.ingestPositions(sessionKey);

        return new IngestionResult("positions", ingested);
    }

    public record IngestionResult(String endpoint, int ingested) {}
}
