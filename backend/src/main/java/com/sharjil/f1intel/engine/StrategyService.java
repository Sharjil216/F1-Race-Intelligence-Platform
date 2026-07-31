package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationResult;
import com.sharjil.f1intel.engine.model.StintShape;
import com.sharjil.f1intel.engine.model.StrategyResult;
import com.sharjil.f1intel.exception.UnsupportedStrategyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final DegradationRepository degradationRepository;
    private final StrategyCalculator strategyCalculator;

    public StrategyService(StrategyRepository strategyRepository, DegradationRepository degradationRepository) {
        this.strategyRepository = strategyRepository;
        this.degradationRepository = degradationRepository;
        this.strategyCalculator = new StrategyCalculator();
    }

    public StrategyResult getStrategy(int sessionKey, int driverNumber) {
        List<StintShape> stintShapes = strategyRepository.stintShapes(sessionKey, driverNumber);

        if (stintShapes.size() != 2) {
            throw new UnsupportedStrategyException("One stop races only; Driver " + driverNumber + " ran " + stintShapes.size() + " stints.");
        }

        StintShape stint1 = stintShapes.get(0);
        StintShape stint2 = stintShapes.get(1);

        String stint1Compound = stint1.compound();
        String stint2Compound = stint2.compound();
        Integer stopLap = stint1.lapEnd();
        int totalLaps = strategyRepository.totalLaps(sessionKey);

        Map<String, DegradationResult> degByCompound = degradationRepository.degradationByCompound(sessionKey)
                .stream()
                .collect(Collectors.toMap(DegradationResult::compound, r -> r));

        DegradationResult deg1 = degByCompound.get(stint1Compound);
        DegradationResult deg2 =  degByCompound.get(stint2Compound);

        if (deg1 == null) {
            throw new UnsupportedStrategyException(stint1Compound + " degradation is not found. Likely due to too few laps in the stint.");
        }

        if (deg2 == null) {
            throw new UnsupportedStrategyException(stint2Compound + " degradation is not found. Likely due to too few laps in the stint.");
        }

        BigDecimal compound1DegSlope = deg1.slope();
        BigDecimal compound2DegSlope = deg2.slope();

        int optimalStopLap = strategyCalculator.optimalStopLap(
                compound1DegSlope.doubleValue(), compound2DegSlope.doubleValue(), totalLaps);

        double actualCost = strategyCalculator.totalCost(
                compound1DegSlope.doubleValue(), compound2DegSlope.doubleValue(), totalLaps, stopLap);

        double optimalCost = strategyCalculator.totalCost(
                compound1DegSlope.doubleValue(), compound2DegSlope.doubleValue(), totalLaps, optimalStopLap);

        double timeDelta = actualCost - optimalCost;

        return new StrategyResult(
                driverNumber, totalLaps,
                stint1Compound, stint2Compound,
                deg1.slope(), deg2.slope(),
                deg1.r2(), deg2.r2(),
                stopLap, optimalStopLap,
                Math.round(actualCost * 1000.0) / 1000.0, Math.round(optimalCost * 1000.0) / 1000.0, Math.round(timeDelta * 1000.0) / 1000.0);
    }
}
