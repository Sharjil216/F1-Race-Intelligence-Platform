package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.PitLossStop;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PitLossRepository {

    private final JdbcClient jdbcClient;

    private static final String SQL = """
    WITH stops AS (
        SELECT
            p.driver_number,
            p.lap_number AS in_lap,
            p.lap_number + 1 AS out_lap
        FROM pit p
        WHERE p.session_key = :sessionKey
        ),
    reference AS (
        SELECT
            s.driver_number,
            s.in_lap,
            s.out_lap,
            pace.reference_lap_duration
        FROM stops s
        LEFT JOIN LATERAL ( SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY l.lap_duration) AS reference_lap_duration
        FROM lap l
        WHERE l.session_key = :sessionKey AND l.driver_number = s.driver_number AND l.lap_number BETWEEN s.in_lap - 4 AND s.in_lap - 1 AND l.lap_duration IS NOT NULL AND l.is_pit_out_lap = false
            AND l.lap_number > 1 ) pace ON TRUE
    ),
    losses AS (
    SELECT
        r.driver_number,
        r.in_lap,
        r.out_lap,
        in_lap.lap_duration AS in_lap_duration,
        out_lap.lap_duration AS out_lap_duration,
        ROUND(r.reference_lap_duration::numeric, 3) AS reference_lap_duration,
    
        ROUND(in_lap.lap_duration - r.reference_lap_duration::numeric, 3)
        AS in_lap_loss,
    
        ROUND(out_lap.lap_duration - r.reference_lap_duration::numeric, 3)
        AS out_lap_loss,
    
        ROUND(
        in_lap.lap_duration
        + out_lap.lap_duration
        - (2 * r.reference_lap_duration::numeric)
            ,3) AS total_pit_loss
    
    FROM reference r
        LEFT JOIN lap in_lap
    ON in_lap.session_key = :sessionKey
        AND in_lap.driver_number = r.driver_number
        AND in_lap.lap_number = r.in_lap
    
        LEFT JOIN lap out_lap
        ON out_lap.session_key = :sessionKey
        AND out_lap.driver_number = r.driver_number
        AND out_lap.lap_number = r.out_lap
    )
    SELECT * FROM losses
    ORDER BY
        in_lap, driver_number;
    """;

    public PitLossRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<PitLossStop> pitLossBySessionKey(int sessionKey) {
        return jdbcClient.sql(SQL)
                .param("sessionKey", sessionKey)
                .query(PitLossStop.class)
                .list();
    }
}
