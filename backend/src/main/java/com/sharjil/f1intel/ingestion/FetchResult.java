package com.sharjil.f1intel.ingestion;

import java.util.List;

public record FetchResult<T>(String rawPayload, List<T> parsed) {}
