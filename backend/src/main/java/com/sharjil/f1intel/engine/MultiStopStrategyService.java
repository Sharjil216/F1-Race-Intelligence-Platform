package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationResult;
import com.sharjil.f1intel.engine.model.Stint;
import com.sharjil.f1intel.engine.model.StintShape;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MultiStopStrategyService {

    private final StrategyRepository strategyRepository;
    private final DegradationRepository degradationRepository;
    private final PitLossService pitLossService;

    //Assumed constant values (for now), for the max laps a single compound can do. These will be derived from the deg curves later.
    private final Map<String, Integer> COMPOUND_LAP_CAPS = Map.of("SOFT", 25, "MEDIUM", 35, "HARD", 45);

    public MultiStopStrategyService(StrategyRepository strategyRepository, DegradationRepository degradationRepository, PitLossService pitLossService) {
        this.strategyRepository = strategyRepository;
        this.degradationRepository = degradationRepository;
        this.pitLossService = pitLossService;
    }

    public MultiStopStrategyResult getMultiStopStrategy(int sessionKey, int driverNumber) {
        int totalLaps = strategyRepository.totalLaps(sessionKey);

        double pitLoss = pitLossService.pitLoss(sessionKey).medianPitLoss().doubleValue();

        Map<String, DegradationResult> degByCompound = degradationRepository.degradationByCompound(sessionKey)
                .stream()
                .collect(Collectors.toMap(DegradationResult::compound, r -> r));

        Map<String, Double> slopes = degByCompound.
                entrySet().stream()
                .collect
                        (Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().slope().doubleValue()));

        List<StintShape> actualShapes = strategyRepository.stintShapes(sessionKey, driverNumber);

        MultiStopOptimiser optimiser = new MultiStopOptimiser(totalLaps, pitLoss, slopes, COMPOUND_LAP_CAPS);

        List<Stint> stints = actualShapes.stream().map(s -> new Stint(s.compound(), s.lapStart(), s.lapEnd())).collect(Collectors.toList());


        MultiStopStrategyResult result = new MultiStopStrategyResult(
                Math.round(optimiser.costOfStrategy(stints) * 1000) / 1000.0,
            optimiser.bestCost(stints.size() - 1),
            optimiser.bestCost(3)
        );

        return result;
    }

    public record MultiStopStrategyResult(double costOfStrategy, MultiStopOptimiser.Result matchedOptimal, MultiStopOptimiser.Result bestOverall) {}
}
