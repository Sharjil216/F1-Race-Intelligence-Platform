package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DegradationService {

    private final DegradationRepository degradationRepository;

    public DegradationService(DegradationRepository degradationRepository) {
        this.degradationRepository = degradationRepository;
    }

    public List<DegradationResult> degradationByCompound(int sessionKey) {
        return degradationRepository.degradationByCompound(sessionKey);
    }
}
