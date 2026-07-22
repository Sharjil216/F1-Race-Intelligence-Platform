package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Position;
import com.sharjil.f1intel.domain.Stint;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface PositionRepository extends Repository<Position, Long> {

    default void upsert(Position p) {
        upsertInternal(p, p.date());
    }

    @Modifying
    @Query("""
    INSERT INTO driver_position (session_key, driver_number, position, position_time)
    VALUES (:#{#p.sessionKey}, :#{#p.driverNumber}, :#{#p.position}, :date)
    ON CONFLICT (session_key, driver_number, position_time) DO UPDATE SET
        position = EXCLUDED.position
    """)
    void upsertInternal(@Param("p") Position p, @Param("date") Instant date);
}