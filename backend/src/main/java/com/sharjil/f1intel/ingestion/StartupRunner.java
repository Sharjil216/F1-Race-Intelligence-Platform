package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Meeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final MeetingIngestionService meetingService;

    public StartupRunner(MeetingIngestionService meetingService) {
        this.meetingService = meetingService;
    }

    @Override
    public void run(String... args) {
        meetingService.ingestMeetings(2024);
    }
}