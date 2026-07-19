package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Stint;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface StintRepository extends Repository<Stint, Long> {

    @Modifying
    @Query("""
    INSERT INTO stint (stint_number, session_key, driver_number, lap_start, lap_end, compound, tyre_age_at_start)
    VALUES (:#{#s.stintNumber}, :#{#s.sessionKey}, :#{#s.driverNumber}, :#{#s.lapStart}, :#{#s.lapEnd}, :#{#s.compound}, :#{#s.tyreAgeAtStart})
    ON CONFLICT (stint_number, session_key, driver_number) DO UPDATE SET
        lap_start = EXCLUDED.lap_start,
        lap_end = EXCLUDED.lap_end,
        compound = EXCLUDED.compound,
        tyre_age_at_start = EXCLUDED.tyre_age_at_start
    """)
    void upsert(@Param("s") Stint s);
}
