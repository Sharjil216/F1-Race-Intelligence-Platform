package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationCurveResult;
import com.sharjil.f1intel.engine.model.DegradationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DegradationService {

    private final DegradationRepository degradationRepository;
    private final DegradationCurveRepository degradationCurveRepository;

    public DegradationService(DegradationRepository degradationRepository, DegradationCurveRepository degradationCurveRepository) {
        this.degradationRepository = degradationRepository;
        this.degradationCurveRepository = degradationCurveRepository;
    }

    public List<DegradationResult> degradationByCompound(int sessionKey) {
        return degradationRepository.degradationByCompound(sessionKey);
    }

    public List<DegradationCurveResult> degradationCurveBySession(int sessionKey) {
        return degradationCurveRepository.degradationCurveBySession(sessionKey);
    }
}
