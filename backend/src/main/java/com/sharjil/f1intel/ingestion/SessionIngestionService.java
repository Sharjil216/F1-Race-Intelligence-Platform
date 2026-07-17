package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Session;
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

    public SessionIngestionService(OpenF1Client openF1Client, SessionRepository sessionRepository) {
        this.openF1Client = openF1Client;
        this.sessionRepository = sessionRepository;
    }

    public int ingestSessions(int meetingKey) {
        List<Session> filteredSessions = openF1Client.fetchSessions(meetingKey).stream().filter(session -> session.sessionKey() != null).toList();

        sessionRepository.saveAll(filteredSessions);
        log.info("Sessions ingested, total count: {}", filteredSessions.size());
        return filteredSessions.size();
    }
}
