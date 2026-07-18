package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Driver;
import com.sharjil.f1intel.domain.Lap;
import com.sharjil.f1intel.repository.DriverRepository;
import com.sharjil.f1intel.repository.LapRepository;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LapIngestionService {

    private static final Logger log = LoggerFactory.getLogger(LapIngestionService.class);

    private final OpenF1Client openF1Client;
    private final LapRepository lapRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public LapIngestionService(OpenF1Client openF1Client, LapRepository lapRepository, RawSnapshotRepository rawSnapshotRepository) {
        this.openF1Client = openF1Client;
        this.lapRepository = lapRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
    }

    public int ingestLaps(int sessionKey) {
        FetchResult<Lap> result = openF1Client.fetchLaps(sessionKey);

        List<Lap> filteredLaps = result.parsed().stream()
                .filter(lap -> lap.sessionKey() != null && lap.driverNumber() != null && lap.lapNumber() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("laps", result.rawPayload(), "session_key=" + sessionKey );
        filteredLaps.forEach(lapRepository::upsert);
        log.info("Ingested {} laps", filteredLaps.size());

        return filteredLaps.size();
    }
}
