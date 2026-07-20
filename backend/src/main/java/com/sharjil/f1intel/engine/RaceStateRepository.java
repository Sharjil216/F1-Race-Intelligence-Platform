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
                       SUM(l.lap_duration) OVER (PARTITION BY l.driver_number ORDER BY lap_number) AS running_total
                FROM lap l
                WHERE session_key = :sessionKey
            )
            SELECT RANK() OVER (PARTITION BY lap_number ORDER BY running_total) AS position,
                    driver_number,
                    running_total - FIRST_VALUE(running_total) OVER (PARTITION BY lap_number ORDER BY running_total) AS gap_to_leader,
                    running_total - LAG(running_total) OVER (PARTITION BY lap_number ORDER BY running_total) AS gap_to_ahead
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
