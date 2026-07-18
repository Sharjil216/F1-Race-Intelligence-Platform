package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.Meeting;
import com.sharjil.f1intel.domain.Session;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends Repository<Session, Integer> {

    @Modifying
    @Query("""
    INSERT INTO session (session_key, meeting_key, session_name, session_type, start_time, end_time)
    VALUES (:#{#s.sessionKey}, :#{#s.meetingKey}, :#{#s.sessionName}, :#{#s.sessionType}, :#{#s.startTimeOffset}, :#{#s.endTimeOffset})
    ON CONFLICT (session_key) DO UPDATE SET
        meeting_key = EXCLUDED.meeting_key,
        session_name = EXCLUDED.session_name,
        session_type = EXCLUDED.session_type,
        start_time = EXCLUDED.start_time,
        end_time = EXCLUDED.end_time
    """)
    void upsert(@Param("s") Session s);
}
