package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Meeting;
import com.sharjil.f1intel.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingIngestionService {

    private static final Logger log =  LoggerFactory.getLogger(MeetingIngestionService.class);

    private final OpenF1Client openF1Client;
    private final MeetingRepository meetingRepository;

    public MeetingIngestionService(OpenF1Client openF1Client, MeetingRepository meetingRepository) {
        this.openF1Client = openF1Client;
        this.meetingRepository = meetingRepository;
    }

    public int ingestMeetings(int year) {
        List<Meeting> meetings = openF1Client.fetchMeetings(year);

        List<Meeting> filteredMeetings = meetings.stream().filter(meeting -> meeting.meetingKey() != null).toList();

        filteredMeetings.forEach(meetingRepository::upsert);
        log.info("Ingested {} meetings", filteredMeetings.size());
        return filteredMeetings.size();
    }
}
