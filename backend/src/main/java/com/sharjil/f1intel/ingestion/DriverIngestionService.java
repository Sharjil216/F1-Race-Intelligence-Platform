package com.sharjil.f1intel.ingestion;

import com.sharjil.f1intel.domain.Driver;
import com.sharjil.f1intel.domain.model.DriverInfo;
import com.sharjil.f1intel.repository.DriverInfoRepository;
import com.sharjil.f1intel.repository.DriverRepository;
import com.sharjil.f1intel.repository.RawSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DriverIngestionService.class);

    private final OpenF1Client openF1Client;
    private final DriverRepository driverRepository;
    private final DriverInfoRepository driverInfoRepository;
    private final RawSnapshotRepository rawSnapshotRepository;

    public DriverIngestionService(OpenF1Client openF1Client, DriverRepository driverRepository, RawSnapshotRepository rawSnapshotRepository, DriverInfoRepository driverInfoRepository) {
        this.openF1Client = openF1Client;
        this.driverRepository = driverRepository;
        this.rawSnapshotRepository = rawSnapshotRepository;
        this.driverInfoRepository = driverInfoRepository;
    }

    public int ingestDrivers(int sessionKey) {
        FetchResult<Driver> result = openF1Client.fetchDrivers(sessionKey);

        List<Driver> filteredDrivers = result.parsed().stream()
                .filter(meeting -> meeting.sessionKey() != null && meeting.driverNumber() != null)
                .toList();

        rawSnapshotRepository.insertSnapshot("drivers", result.rawPayload(), "session_key=" + sessionKey );
        filteredDrivers.forEach(driverRepository::upsert);
        log.info("Ingested {} drivers", filteredDrivers.size());

        return filteredDrivers.size();
    }

    public List<DriverInfo> getDrivers(int sessionKey) {
        return driverInfoRepository.getDriverInfo(sessionKey);
    }
}
