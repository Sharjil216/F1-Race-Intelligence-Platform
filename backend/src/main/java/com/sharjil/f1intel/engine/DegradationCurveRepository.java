package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationCurveResult;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DegradationCurveRepository {

    private final JdbcClient jdbcClient;

    private static final String SQL = """
            WITH stint_laps AS (
                SELECT s.driver_number,
                       s.stint_number,
                       s.compound,
                       l.lap_number,
                       l.lap_duration,
                       s.tyre_age_at_start + (l.lap_number - s.lap_start) AS tyre_age,
                       l.lap_duration + 0.035 * l.lap_number              AS fuel_corrected
                FROM stint s
                    JOIN lap l
                        ON  l.session_key   = s.session_key
                        AND l.driver_number = s.driver_number
                        AND l.lap_number > s.lap_start
                        AND l.lap_number < s.lap_end
                WHERE s.session_key = :sessionKey
                  AND l.lap_duration IS NOT NULL
            ),
            stint_stats AS (
                SELECT driver_number,
                        stint_number,
                        AVG(fuel_corrected)                                       AS stint_avg,
                        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY lap_duration) AS stint_median
                FROM stint_laps
                GROUP BY driver_number, stint_number
            )
            SELECT sl.compound,
                   (sl.tyre_age / 3) * 3                                    AS age_bucket,
                   COUNT(*)                                                 AS laps,
                   ROUND(AVG(sl.fuel_corrected - ss.stint_avg)::numeric, 3) AS avg_delta
            FROM stint_laps sl
                JOIN stint_stats ss
                    ON  ss.driver_number = sl.driver_number
                    AND ss.stint_number  = sl.stint_number
            WHERE sl.lap_duration <= 1.07 * ss.stint_median
            GROUP BY sl.compound, (sl.tyre_age / 3) * 3
            ORDER BY sl.compound, age_bucket;
            """;

    public DegradationCurveRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<DegradationCurveResult> degradationCurveBySession(int sessionKey) {
        return jdbcClient.sql(SQL)
                .param("sessionKey", sessionKey)
                .query(DegradationCurveResult.class)
                .list();
    }
}
