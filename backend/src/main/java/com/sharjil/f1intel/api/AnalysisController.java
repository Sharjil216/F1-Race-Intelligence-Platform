package com.sharjil.f1intel.api;

import com.sharjil.f1intel.engine.DegradationService;
import com.sharjil.f1intel.engine.PitLossRepository;
import com.sharjil.f1intel.engine.PitLossService;
import com.sharjil.f1intel.engine.RaceStateRepository;
import com.sharjil.f1intel.engine.model.DegradationCurveResult;
import com.sharjil.f1intel.engine.model.DegradationResult;
import com.sharjil.f1intel.engine.model.PitLossSummary;
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
    private final PitLossService pitLossService;

    public AnalysisController(DegradationService degradationService, RaceStateRepository raceStateRepository,  PitLossService pitLossService) {
        this.degradationService = degradationService;
        this.raceStateRepository = raceStateRepository;
        this.pitLossService = pitLossService;
    }

    @GetMapping("/degradation")
    public List<DegradationResult> degradationByCompound(@RequestParam int sessionKey) {
        return degradationService.degradationByCompound(sessionKey);
    }

    @GetMapping("/degradation-curve")
    public List<DegradationCurveResult> degradationCurveBySession(@RequestParam int sessionKey) {
        return degradationService.degradationCurveBySession(sessionKey);
    }

    @GetMapping("/race-state")
    public List<RaceStateResult> raceStateBySessionAndLap(@RequestParam int sessionKey, @RequestParam int lap) {
        return raceStateRepository.raceStateBySessionAndLap(sessionKey, lap);
    }

    @GetMapping("/pit-loss")
    public PitLossSummary pitLossSummaryBySessionKey(@RequestParam int sessionKey) {
        return pitLossService.pitLoss(sessionKey);
    }
}
