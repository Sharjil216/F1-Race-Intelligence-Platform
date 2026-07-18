package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Driver;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DriverRepository extends CrudRepository<Driver, Long> {

    @Modifying
    @Query("""
    INSERT INTO driver (driver_number, session_key, full_name, name_acronym, team_name, country_code, team_colour)
    VALUES (:#{#d.driverNumber}, :#{#d.sessionKey}, :#{#d.fullName}, :#{#d.nameAcronym}, :#{#d.teamName}, :#{#d.countryCode}, :#{#d.teamColour})
    ON CONFLICT (session_key, driver_number) DO UPDATE SET
        full_name = EXCLUDED.full_name,
        name_acronym = EXCLUDED.name_acronym,
        team_name = EXCLUDED.team_name,
        country_code = EXCLUDED.country_code,
        team_colour = EXCLUDED.team_colour
    """)
    void upsert(@Param("d") Driver d);
}
