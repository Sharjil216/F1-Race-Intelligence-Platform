package com.sharjil.f1intel.api;

import com.sharjil.f1intel.engine.*;
import com.sharjil.f1intel.engine.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final DegradationService degradationService;
    private final RaceStateService raceStateService;
    private final PitLossService pitLossService;
    private final StrategyService strategyService;
    private final MultiStopStrategyService multiStopStrategyService;

    public AnalysisController(DegradationService degradationService, RaceStateService raceStateService,  PitLossService pitLossService, StrategyService strategyService,  MultiStopStrategyService multiStopStrategyService) {
        this.degradationService = degradationService;
        this.raceStateService = raceStateService;
        this.pitLossService = pitLossService;
        this.strategyService = strategyService;
        this.multiStopStrategyService = multiStopStrategyService;
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

    @GetMapping("/strategy")
    public StrategyResult strategyResultBySessionAndDriver (@RequestParam int sessionKey, @RequestParam int driverNumber) {
        return strategyService.getStrategy(sessionKey, driverNumber);
    }

    @GetMapping("/multi-stop-strategy")
    public MultiStopStrategyService.MultiStopStrategyResult multiStopStrategyBySessionKey(@RequestParam int sessionKey, @RequestParam int driverNumber) {
        return multiStopStrategyService.getMultiStopStrategy(sessionKey, driverNumber);
    }
}
