package com.sharjil.f1intel.ingestion;


import com.sharjil.f1intel.domain.Meeting;
import com.sharjil.f1intel.domain.RawSnapshot;
import com.sharjil.f1intel.repository.MeetingRepository;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingIngestionService {

    private static final Logger log =  LoggerFactory.getLogger(MeetingIngestionService.class);

    private final OpenF1Client openF1Client;
    private final MeetingRepository meetingRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public MeetingIngestionService(OpenF1Client openF1Client, MeetingRepository meetingRepository, RawSnapshotRepository rawSnapshotRepository) {
        this.openF1Client = openF1Client;
        this.meetingRepository = meetingRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
    }

    public int ingestMeetings(int year) {
        FetchResult<Meeting> result = openF1Client.fetchMeetings(year);

        List<Meeting> filteredMeetings = result.parsed().stream()
                .filter(meeting -> meeting.meetingKey() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("meetings", result.rawPayload(), "year=" + year);
        filteredMeetings.forEach(meetingRepository::upsert);
        log.info("Ingested {} meetings", filteredMeetings.size());

        return filteredMeetings.size();
    }
}
