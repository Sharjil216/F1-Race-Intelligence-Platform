package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.PitLossStop;
import com.sharjil.f1intel.engine.model.PitLossSummary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class PitLossService {

    private final PitLossRepository pitLossRepository;

    public PitLossService(PitLossRepository pitLossRepository) {
        this.pitLossRepository = pitLossRepository;
    }

    public PitLossSummary pitLoss(int sessionKey) {
        List<PitLossStop> stops = pitLossRepository.pitLossBySessionKey(sessionKey);
        BigDecimal medianPitLoss = medianOf(stops);
        return new PitLossSummary(medianPitLoss, stops.size(), stops);

    }

    private BigDecimal medianOf(List<PitLossStop> stops) {
        List<BigDecimal> sorted = stops.stream()
                .filter(Objects::nonNull)
                .map(PitLossStop::totalPitLoss)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        if (sorted.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int middle = sorted.size() / 2;

        if (sorted.size() % 2 == 1) {
            return sorted.get(middle);
        }

        return sorted.get(middle - 1)
                .add(sorted.get(middle))
                .divide(BigDecimal.TWO);
    }
}
