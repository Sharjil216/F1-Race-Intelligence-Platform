package com.sharjil.f1intel.api;

import com.sharjil.f1intel.engine.*;
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
    private final RaceStateService raceStateService;
    private final PitLossService pitLossService;

    public AnalysisController(DegradationService degradationService, RaceStateService raceStateService,  PitLossService pitLossService) {
        this.degradationService = degradationService;
        this.raceStateService = raceStateService;
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
        return raceStateService.raceStateBySessionKeyAndLap(sessionKey, lap);
    }

    @GetMapping("/pit-loss")
    public PitLossSummary pitLossSummaryBySessionKey(@RequestParam int sessionKey) {
        return pitLossService.pitLoss(sessionKey);
    }
}
