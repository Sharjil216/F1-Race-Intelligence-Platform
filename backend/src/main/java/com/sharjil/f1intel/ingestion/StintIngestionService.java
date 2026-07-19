package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Lap;
import com.sharjil.f1intel.domain.Stint;
import com.sharjil.f1intel.repository.LapRepository;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import com.sharjil.f1intel.repository.StintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StintIngestionService {

    private static final Logger log = LoggerFactory.getLogger(StintIngestionService.class);

    private final OpenF1Client openF1Client;
    private final StintRepository stintRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public StintIngestionService(OpenF1Client openF1Client, StintRepository stintRepository, RawSnapshotRepository rawSnapshotRepository) {
        this.openF1Client = openF1Client;
        this.stintRepository = stintRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
    }

    public int ingestStints(int sessionKey) {
        FetchResult<Stint> result = openF1Client.fetchStints(sessionKey);

        List<Stint> filteredStints = result.parsed().stream()
                .filter(stint -> stint.sessionKey() != null && stint.driverNumber() != null && stint.stintNumber() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("stints", result.rawPayload(), "session_key=" + sessionKey );
        filteredStints.forEach(stintRepository::upsert);
        log.info("Ingested {} stints", filteredStints.size());

        return filteredStints.size();
    }
}
