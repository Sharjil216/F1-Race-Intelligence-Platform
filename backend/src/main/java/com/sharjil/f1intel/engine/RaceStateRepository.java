package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationResult;
import com.sharjil.f1intel.engine.model.RaceStateResult;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RaceStateRepository {

    private final JdbcClient jdbcClient;

    private static final String SQL = """
             WITH lap_totals AS (
                SELECT l.lap_number,
                       l.driver_number,
                       l.lap_duration,
                       l.date_start + (l.lap_duration * INTERVAL '1 second') AS lap_end_time
                FROM lap l
                WHERE session_key = :sessionKey
            )
            SELECT RANK() OVER (PARTITION BY lap_number ORDER BY lap_end_time) AS position,
                    driver_number,
                    ROUND( EXTRACT(EPOCH FROM (lap_end_time - FIRST_VALUE(lap_end_time)
                            OVER (PARTITION BY lap_number ORDER BY lap_end_time)))::numeric, 3) AS gap_to_leader,
                    ROUND ( EXTRACT(EPOCH FROM (lap_end_time - LAG(lap_end_time)
                            OVER (PARTITION BY lap_number ORDER BY lap_end_time)))::numeric, 3) AS gap_to_ahead
            FROM lap_totals
            WHERE lap_number = :lapNumber
            ORDER BY position;
            """;

    public RaceStateRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<RaceStateResult> raceStateBySessionAndLap(int sessionKey, int lapNumber) {
        return jdbcClient.sql(SQL)
                .param("sessionKey", sessionKey)
                .param("lapNumber", lapNumber)
                .query(RaceStateResult.class)
                .list();
    }
}
