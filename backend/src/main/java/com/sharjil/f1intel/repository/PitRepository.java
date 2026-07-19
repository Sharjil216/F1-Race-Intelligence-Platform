package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Pit;
import com.sharjil.f1intel.domain.Stint;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface PitRepository extends Repository<Pit, Long> {

    default void upsert(Pit pit) {
        upsertInternal(pit, pit.pitTime());
    }

    @Modifying
    @Query("""
    INSERT INTO pit (lap_number, session_key, driver_number, pit_time, stop_duration, pit_duration, lane_duration)
    VALUES (:#{#p.lapNumber}, :#{#p.sessionKey}, :#{#p.driverNumber}, :pitTime, :#{#p.stopDuration}, :#{#p.pitDuration}, :#{#p.laneDuration})
    ON CONFLICT (lap_number, session_key, driver_number) DO UPDATE SET
        pit_time = EXCLUDED.pit_time,
        stop_duration = EXCLUDED.stop_duration,
        pit_duration = EXCLUDED.pit_duration,
        lane_duration = EXCLUDED.lane_duration
    """)
    void upsertInternal(@Param("p") Pit p, @Param("pitTime") Instant pitTime);
}
