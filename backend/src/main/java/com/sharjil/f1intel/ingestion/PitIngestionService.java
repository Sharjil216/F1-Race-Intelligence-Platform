package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Pit;
import com.sharjil.f1intel.domain.Stint;
import com.sharjil.f1intel.repository.PitRepository;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import com.sharjil.f1intel.repository.StintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PitIngestionService {

    private static final Logger log = LoggerFactory.getLogger(PitIngestionService.class);

    private final OpenF1Client openF1Client;
    private final PitRepository pitRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public PitIngestionService(OpenF1Client openF1Client, PitRepository pitRepository, RawSnapshotRepository rawSnapshotRepository) {
        this.openF1Client = openF1Client;
        this.pitRepository = pitRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
    }

    public int ingestPits(int sessionKey) {
        FetchResult<Pit> result = openF1Client.fetchPits(sessionKey);

        List<Pit> filteredPits = result.parsed().stream()
                .filter(pit -> pit.sessionKey() != null && pit.driverNumber() != null && pit.lapNumber() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("pits", result.rawPayload(), "session_key=" + sessionKey );
        filteredPits.forEach(pitRepository::upsert);
        log.info("Ingested {} pits", filteredPits.size());

        return filteredPits.size();
    }
}
