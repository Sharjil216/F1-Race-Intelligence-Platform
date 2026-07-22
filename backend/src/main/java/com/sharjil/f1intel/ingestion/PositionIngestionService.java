package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Position;
import com.sharjil.f1intel.domain.Stint;
import com.sharjil.f1intel.repository.PositionRepository;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import com.sharjil.f1intel.repository.StintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionIngestionService {

    private static final Logger log = LoggerFactory.getLogger(PositionIngestionService.class);

    private final OpenF1Client openF1Client;
    private final PositionRepository positionRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public PositionIngestionService(OpenF1Client openF1Client, PositionRepository positionRepository, RawSnapshotRepository rawSnapshotRepository) {
        this.openF1Client = openF1Client;
        this.positionRepository = positionRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
    }

    public int ingestPositions(int sessionKey) {
        FetchResult<Position> result = openF1Client.fetchPosition(sessionKey);

        List<Position> filteredPositions = result.parsed().stream()
                .filter(pos -> pos.sessionKey() != null && pos.driverNumber() != null && pos.date() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("positions", result.rawPayload(), "session_key=" + sessionKey );
        filteredPositions.forEach(positionRepository::upsert);
        log.info("Ingested {} positions", filteredPositions.size());

        return filteredPositions.size();
    }
}
