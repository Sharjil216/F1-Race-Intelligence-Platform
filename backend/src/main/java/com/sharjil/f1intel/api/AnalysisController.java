package com.sharjil.f1intel.api;

import com.sharjil.f1intel.engine.DegradationService;
import com.sharjil.f1intel.engine.RaceStateRepository;
import com.sharjil.f1intel.engine.model.DegradationResult;
import com.sharjil.f1intel.engine.model.RaceStateResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final DegradationService degradationService;
    private final RaceStateRepository raceStateRepository;

    public AnalysisController(DegradationService degradationService, RaceStateRepository raceStateRepository) {
        this.degradationService = degradationService;
        this.raceStateRepository = raceStateRepository;
    }

    @GetMapping("/degradation")
    public List<DegradationResult> degradationByCompound(@RequestParam int sessionKey) {
        return degradationService.degradationByCompound(sessionKey);
    }

    @GetMapping("/race-state")
    public List<RaceStateResult> raceStateBySessionAndLap(@RequestParam int sessionKey, @RequestParam int lap) {
        return raceStateRepository.raceStateBySessionAndLap(sessionKey, lap);
    }
}
