package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.RaceStateResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceStateService {

    private final RaceStateRepository raceStateRepository;

    public RaceStateService(RaceStateRepository raceStateRepository) {
        this.raceStateRepository = raceStateRepository;
    }

    public List<RaceStateResult> raceStateBySessionKeyAndLap(int sessionKey, int lap) {
        return raceStateRepository.raceStateBySessionAndLap(sessionKey, lap);
    }
}
