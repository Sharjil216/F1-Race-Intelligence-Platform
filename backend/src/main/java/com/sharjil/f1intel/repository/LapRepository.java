package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Driver;
import com.sharjil.f1intel.domain.Lap;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface LapRepository extends Repository<Lap, Long> {

    @Modifying
    @Query("""
    INSERT INTO lap (lap_number, driver_number, session_key, date_start, lap_duration, duration_sector_1, duration_sector_2, duration_sector_3, i1_speed, i2_speed, st_speed, is_pit_out_lap)
    VALUES (:#{#l.lapNumber}, :#{#l.driverNumber}, :#{#l.sessionKey}, :#{#l.dateStartOffset}, :#{#l.lapDuration}, :#{#l.durationSector1}, :#{#l.durationSector2}, :#{#l.durationSector3}, :#{#l.i1Speed}, :#{#l.i2Speed}, :#{#l.stSpeed}, :#{#l.isPitOutLap})
    ON CONFLICT (session_key, driver_number, lap_number) DO UPDATE SET
        date_start = EXCLUDED.date_start,
        lap_duration = EXCLUDED.lap_duration,
        duration_sector_1 = EXCLUDED.duration_sector_1,
        duration_sector_2 = EXCLUDED.duration_sector_2,
        duration_sector_3 = EXCLUDED.duration_sector_3,
        i1_speed = EXCLUDED.i1_speed,
        i2_speed = EXCLUDED.i2_speed,
        st_speed = EXCLUDED.st_speed,
        is_pit_out_lap = EXCLUDED.is_pit_out_lap
    """)
    void upsert(@Param("l") Lap l);
}
