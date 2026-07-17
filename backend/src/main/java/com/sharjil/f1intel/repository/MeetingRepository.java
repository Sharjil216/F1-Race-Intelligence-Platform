package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Meeting;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends CrudRepository<Meeting,Integer> {

    @Modifying
    @Query("""
    INSERT INTO meeting (meeting_key, year, meeting_name, country_name,
                             circuit_short_name, meeting_official_name, location,
                             date_start, date_end)
    VALUES (:#{#m.meetingKey}, :#{#m.year}, :#{#m.meetingName}, :#{#m.countryName}, :#{#m.circuitShortName}, :#{#m.meetingOfficialName}, :#{#m.location}, :#{#m.dateStartOffset}, :#{#m.dateEndOffset})
    ON CONFLICT (meeting_key) DO UPDATE SET
        year = EXCLUDED.year,
        meeting_name = EXCLUDED.meeting_name,
        country_name = EXCLUDED.country_name,
        circuit_short_name = EXCLUDED.circuit_short_name,
        meeting_official_name = EXCLUDED.meeting_official_name,
        location = EXCLUDED.location,
        date_start = EXCLUDED.date_start,
        date_end = EXCLUDED.date_end
    """)
    void upsert(@Param("m") Meeting m);
}
