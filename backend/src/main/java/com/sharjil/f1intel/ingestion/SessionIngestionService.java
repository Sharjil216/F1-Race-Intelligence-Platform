package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Session;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import com.sharjil.f1intel.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionIngestionService {

    private static final Logger log =  LoggerFactory.getLogger(SessionIngestionService.class);

    private final OpenF1Client openF1Client;
    private final SessionRepository sessionRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public SessionIngestionService(OpenF1Client openF1Client, SessionRepository sessionRepository, RawSnapshotRepository rawSnapshotRepository) {
        this.openF1Client = openF1Client;
        this.sessionRepository = sessionRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
    }

    public int ingestSessions(int meetingKey) {
        FetchResult<Session> result = openF1Client.fetchSessions(meetingKey);   // ONE call

        List<Session> filteredSession = result.parsed().stream()
                .filter(session -> session.sessionKey() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("sessions", result.rawPayload(), "meeting_key=" + meetingKey);
        filteredSession.forEach(sessionRepository::upsert);
        log.info("Ingested {} sessions", filteredSession.size());

        return filteredSession.size();
    }
}
